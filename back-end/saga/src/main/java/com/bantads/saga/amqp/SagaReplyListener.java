package com.bantads.saga.amqp;

import com.bantads.saga.config.RabbitConfig;
import com.bantads.saga.dto.SagaMessage;
import com.bantads.saga.entity.SagaState;
import com.bantads.saga.repository.SagaStateRepository;
import com.bantads.saga.orchestrator.AutocadastroSagaOrchestrator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SagaReplyListener {

    private final SagaStateRepository sagaStateRepository;
    private final AutocadastroSagaOrchestrator autocadastroSagaOrchestrator;

    public SagaReplyListener(SagaStateRepository sagaStateRepository, AutocadastroSagaOrchestrator autocadastroSagaOrchestrator) {
        this.sagaStateRepository = sagaStateRepository;
        this.autocadastroSagaOrchestrator = autocadastroSagaOrchestrator;
    }

    @RabbitListener(queues = RabbitConfig.SAGA_REPLY_QUEUE)
    public void onMessage(SagaMessage message) {
        System.out.println("[SAGA] Resposta recebida: " + message.getTipoSaga() + " -> " + message.getAcao() + " (Sucesso: " + message.getSucesso() + ")");

        Optional<SagaState> stateOpt = sagaStateRepository.findBySagaId(message.getSagaId());
        
        if (stateOpt.isEmpty()) {
            System.err.println("[SAGA] Erro: Saga nÃ£o encontrada para ID: " + message.getSagaId());
            return;
        }

        SagaState sagaState = stateOpt.get();

        if (AutocadastroSagaOrchestrator.TIPO_SAGA.equals(message.getTipoSaga())) {
            autocadastroSagaOrchestrator.handleReply(sagaState, message);
        }
        
        // TODO: Adicionar os IFs mestre para as Sagas de (ALERACAO_PERFIL, INSERCAO_GERENTE, REMOCAO_GERENTE)
    }
}