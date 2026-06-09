package com.bantads.saga.amqp;

import com.bantads.saga.config.RabbitConfig;
import com.bantads.saga.dto.SagaMessage;
import com.bantads.saga.orchestrator.AutocadastroSagaOrchestrator;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SagaRequestListener {

    private final AutocadastroSagaOrchestrator autocadastroOrchestrator;

    public SagaRequestListener(AutocadastroSagaOrchestrator autocadastroOrchestrator) {
        this.autocadastroOrchestrator = autocadastroOrchestrator;
    }

    /**
     * Escuta a fila saga-request-queue.
     * Os microsserviços borda (ex: MS-Cliente) colocarão as mensagens aqui para iniciar a Saga.
     */
    @RabbitListener(queues = RabbitConfig.SAGA_REQUEST_QUEUE)
    public void onSagaRequest(SagaMessage request) {
        System.out.println("[SAGA] Pedido para iniciar SAGA recebido: " + request.getTipoSaga());

        if (AutocadastroSagaOrchestrator.TIPO_SAGA.name().equals(request.getTipoSaga())) {
            autocadastroOrchestrator.iniciarSaga(request.getPayload());
        }
        
        // TODO: Mapear 'ALTERACAO_PERFIL', 'INSERCAO_GERENTE', 'REMOCAO_GERENTE'
    }
}