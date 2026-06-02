package com.bantads.conta_service.service

import com.bantads.conta_service.dto.ContaDTO
import com.bantads.conta_service.dto.ContaDetalhesDTO
import com.bantads.conta_service.dto.ContaWriteDTO
import com.bantads.conta_service.entity.comando.Conta
import com.bantads.conta_service.entity.leitura.Conta as ContaLeitura
import com.bantads.conta_service.repository.comando.ContaRepositoryWrite
import com.bantads.conta_service.repository.comando.TransferenciaRepositoryWrite
import com.bantads.conta_service.repository.leitura.ContaRepositoryRead
import com.bantads.conta_service.repository.leitura.TransferenciaRepositoryRead
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random
import org.springframework.amqp.rabbit.core.RabbitTemplate
import com.bantads.conta_service.config.CQRS_EVENT_EXCHANGE

@Service
@Transactional
class ContaService(
    private val contaRepositoryRead: ContaRepositoryRead,
    private val transferenciaRepositoryRead: TransferenciaRepositoryRead,
    private val contaRepositoryWrite: ContaRepositoryWrite,
    private val transferenciaRepositoryWrite: TransferenciaRepositoryWrite,
    private val rabbitTemplate: RabbitTemplate
) {

    fun criar(numero: String, request: ContaDTO): Any {
        if (request.cliente.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente é obrigatório")
        }

        if (request.saldo < BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo inicial não pode ser negativo")
        }

        if (request.limite < BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Limite não pode ser negativo")
        }

        if (contaRepositoryRead.findByNumero(numero) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Número de conta já existe")
        }

        val conta = contaRepositoryWrite.save(
            Conta(
                cliente = request.cliente,
                numero = numero,
                saldo = request.saldo.setScale(2, RoundingMode.HALF_EVEN),
                limite = request.limite.setScale(2, RoundingMode.HALF_EVEN),
                gerente = request.gerente,
                criacao = request.criacao
            )
        )

        val eventoCqrs = ContaWriteDTO(
            cliente = request.cliente,
            numero = numero,
            saldo = request.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = request.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = request.gerente,
            criacao = request.criacao
        )
        rabbitTemplate.convertAndSend(CQRS_EVENT_EXCHANGE, "cqrs.event.conta", eventoCqrs)

        return conta
    }

    fun criarContaRead(conta: ContaWriteDTO)
    {
        contaRepositoryRead.save(
            ContaLeitura(
                cliente = conta.cliente,
                numero = conta.numero,
                saldo = conta.saldo,
                limite = conta.limite,
                gerente = conta.gerente,
                criacao = conta.criacao
            )
        )
    }

    fun atualizarGerente(numero: String, gerenteCpf: String): ContaDetalhesDTO {
        if (gerenteCpf.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF do gerente é obrigatório")
        }

        val conta = contaRepositoryWrite.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        conta.gerente = gerenteCpf
        val contaAtualizada = contaRepositoryWrite.save(conta)

        contaRepositoryRead.save(
            ContaLeitura(
                cliente = contaAtualizada.cliente,
                numero = contaAtualizada.numero,
                saldo = contaAtualizada.saldo,
                limite = contaAtualizada.limite,
                gerente = contaAtualizada.gerente,
                criacao = contaAtualizada.criacao
            )
        )

        return ContaDetalhesDTO(
            cliente = contaAtualizada.cliente,
            numero = contaAtualizada.numero,
            saldo = contaAtualizada.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = contaAtualizada.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = contaAtualizada.gerente,
            criacao = contaAtualizada.criacao.toString()
        )
    }

    fun gerarNumeroContaUnico(): String {
        var numero: String
        do {
            numero = Random.nextInt(1000, 10000).toString()
        } while (contaRepositoryRead.findByNumero(numero) != null)
        return numero
    }

    fun calcularLimite(salario: BigDecimal): BigDecimal {
        val metadeSalario = salario.setScale(2, RoundingMode.HALF_EVEN).divide(BigDecimal(2), RoundingMode.HALF_EVEN)
        return if (metadeSalario > BigDecimal(2000).setScale(2, RoundingMode.HALF_EVEN)) {
            BigDecimal(2000).setScale(2, RoundingMode.HALF_EVEN)
        } else {
            metadeSalario
        }
    }

    fun obterContaPorCliente(cpf: String): ContaDetalhesDTO? {
        val conta = contaRepositoryRead.findFirstByCliente(cpf) ?: return null
        return ContaDetalhesDTO(
            cliente = conta.cliente,
            numero = conta.numero,
            saldo = conta.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = conta.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = conta.gerente,
            criacao = conta.criacao.toString()
        )
    }

    fun obterContasPorGerente(cpfGerente: String): List<ContaDetalhesDTO> {
        return contaRepositoryRead.findByGerente(cpfGerente)
            .map { conta ->
                ContaDetalhesDTO(
                    cliente = conta.cliente,
                    numero = conta.numero,
                    saldo = conta.saldo.setScale(2, RoundingMode.HALF_EVEN),
                    limite = conta.limite.setScale(2, RoundingMode.HALF_EVEN),
                    gerente = conta.gerente,
                    criacao = conta.criacao.toString()
                )
            }
    }

    fun obterTop3Contas(): List<ContaDetalhesDTO> {
        return contaRepositoryRead.findTop3ByOrderBySaldoDesc()
            .map { conta ->
                ContaDetalhesDTO(
                    cliente = conta.cliente,
                    numero = conta.numero,
                    saldo = conta.saldo.setScale(2, RoundingMode.HALF_EVEN),
                    limite = conta.limite.setScale(2, RoundingMode.HALF_EVEN),
                    gerente = conta.gerente,
                    criacao = conta.criacao.toString()
                )
            }
    }
}
