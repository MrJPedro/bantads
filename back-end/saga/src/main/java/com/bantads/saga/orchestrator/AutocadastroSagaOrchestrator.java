package com.bantads.saga.orchestrator;

import com.bantads.saga.config.RabbitConfig;
import com.bantads.saga.dto.SagaMessage;
import com.bantads.saga.entity.SagaState;
import com.bantads.saga.entity.TipoSaga;
import com.bantads.saga.entity.SagaStatus;
import com.bantads.saga.repository.SagaStateRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AutocadastroSagaOrchestrator {

    private final SagaStateRepository sagaStateRepository;
    private final RabbitTemplate rabbitTemplate;

    public AutocadastroSagaOrchestrator(SagaStateRepository sagaStateRepository, RabbitTemplate rabbitTemplate) {
        this.sagaStateRepository = sagaStateRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Passos/Estados da Saga
    public static final TipoSaga TIPO_SAGA = TipoSaga.AUTOCADASTRO;
    public static final String ESTADO_INICIAL = "INICIADO";
    public static final String ESTADO_AUTH_PENDENTE = "AGUARDANDO_AUTH";
    public static final String ESTADO_GERENTE_PENDENTE = "AGUARDANDO_GERENTE";
    public static final String ESTADO_CONTA_PENDENTE = "AGUARDANDO_CONTA";
    public static final String ESTADO_SUCESSO = "FINALIZADO_COM_SUCESSO";
    public static final String ESTADO_ERRO = "FALHA_COM_ROLLBACK";

    // Ações (Comandos enviados para os microsserviços)
    public static final String ACAO_CRIAR_AUTH = "CRIAR_AUTH";
    public static final String ACAO_OBTER_GERENTE = "OBTER_GERENTE_DISPONIVEL";
    public static final String ACAO_CRIAR_CONTA = "CRIAR_CONTA_INICIAL";
    public static final String ACAO_ROLLBACK_CLIENTE = "ROLLBACK_CLIENTE";
    public static final String ACAO_ROLLBACK_AUTH = "ROLLBACK_AUTH";

    /**
     * Inicia a saga de Autocadastro.
     * Normalmente chamado pelo Controller do MS Saga, ou via mensagem do MS Cliente.
     */
    public void iniciarSaga(String clientePayload) {
        UUID sagaId = UUID.randomUUID();

        SagaState state = new SagaState();
        state.setSagaId(sagaId);
        state.setTipoSaga(TIPO_SAGA);
        state.setEstadoAtual(ESTADO_AUTH_PENDENTE);
        state.setStatus(SagaStatus.PENDENTE);
        sagaStateRepository.save(state);

        System.out.println("[SAGA AUTOCADASTRO] Iniciando saga " + sagaId + ". Enviando comando para MS-Auth.");

        // Passo 1: Enviar comando para MS Auth criar o usuário/senha (ainda inativo)
        SagaMessage comando = new SagaMessage(sagaId, TIPO_SAGA.name(), ACAO_CRIAR_AUTH, null, clientePayload);
        rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "auth.command", comando);
    }

    /**
     * Controla a máquina de estados baseado na resposta recebida.
     */
    public void handleReply(SagaState state, SagaMessage reply) {
        System.out.println("[SAGA AUTOCADASTRO] Processando estado atual: " + state.getEstadoAtual() + " | Sucesso: " + reply.getSucesso());

        if (!reply.getSucesso()) {
            iniciarRollback(state, reply);
            return;
        }

        switch (state.getEstadoAtual()) {
            case ESTADO_AUTH_PENDENTE:
                // MS-Auth criou com sucesso. Próximo passo: Pedir um gerente para o MS-Gerente ou MS-Conta.
                state.setEstadoAtual(ESTADO_GERENTE_PENDENTE);
                sagaStateRepository.save(state);
                
                System.out.println("[SAGA AUTOCADASTRO] Auth concluído. Solicitando MS-Gerente...");
                SagaMessage comandoGerente = new SagaMessage(state.getSagaId(), TIPO_SAGA.name(), ACAO_OBTER_GERENTE, null, reply.getPayload());
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "gerente.command", comandoGerente);
                break;

            case ESTADO_GERENTE_PENDENTE:
                // Gerente obtido. Próximo passo: Criar a Conta (Status PENDENTE DE APROVAÇÃO)
                state.setEstadoAtual(ESTADO_CONTA_PENDENTE);
                sagaStateRepository.save(state);

                System.out.println("[SAGA AUTOCADASTRO] Gerente definido. Solicitando criação ao MS-Conta...");
                SagaMessage comandoConta = new SagaMessage(state.getSagaId(), TIPO_SAGA.name(), ACAO_CRIAR_CONTA, null, reply.getPayload());
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "conta.command", comandoConta);
                break;

            case ESTADO_CONTA_PENDENTE:
                // Conta criada e aguardando aprovação. A Saga terminou com sucesso.
                state.setEstadoAtual(ESTADO_SUCESSO);
                state.setStatus(SagaStatus.SUCESSO);
                sagaStateRepository.save(state);

                System.out.println("[SAGA AUTOCADASTRO] Concluída com SUCESSO! Saga ID: " + state.getSagaId());
                break;

            default:
                System.out.println("[SAGA AUTOCADASTRO] Estado desconhecido ou já finalizado.");
        }
    }

    private void iniciarRollback(SagaState state, SagaMessage errorReply) {
        System.out.println("[SAGA AUTOCADASTRO ROLLBACK] Falha no passo: " + state.getEstadoAtual() + ". Motivo: " + errorReply.getPayload());
        
        state.setStatus(SagaStatus.REVERTIDO);

        // Dependendo de onde falhou, mandamos as mensagens de compensação
        switch (state.getEstadoAtual()) {
            case ESTADO_CONTA_PENDENTE:
                // Falhou na conta: manda o Auth desfazer
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "auth.command", 
                    new SagaMessage(state.getSagaId(), TIPO_SAGA.name(), ACAO_ROLLBACK_AUTH, null, errorReply.getPayload()));
                // Manda o Cliente desfazer (apagar o registro no banco Cliente)
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "cliente.command", 
                    new SagaMessage(state.getSagaId(), TIPO_SAGA.name(), ACAO_ROLLBACK_CLIENTE, null, errorReply.getPayload()));
                break;
            case ESTADO_GERENTE_PENDENTE:
                // Falhou no gerente: manda Auth e Cliente desfazerem
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "auth.command", 
                    new SagaMessage(state.getSagaId(), TIPO_SAGA.name(), ACAO_ROLLBACK_AUTH, null, errorReply.getPayload()));
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "cliente.command",
                    new SagaMessage(state.getSagaId(), TIPO_SAGA.name(), ACAO_ROLLBACK_CLIENTE, null, errorReply.getPayload()));
                break;
            case ESTADO_AUTH_PENDENTE:
                // Falhou no Auth: Manda o MS Cliente desfazer o registro inicial
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "cliente.command", 
                    new SagaMessage(state.getSagaId(), TIPO_SAGA.name(), ACAO_ROLLBACK_CLIENTE, null, errorReply.getPayload()));
                break;
        }

        state.setEstadoAtual(ESTADO_ERRO);
        sagaStateRepository.save(state);
    }
}