package com.bantads.conta_service.service

import com.bantads.conta_service.config.CQRS_EVENT_EXCHANGE
import com.bantads.conta_service.dto.ContaWriteDTO
import com.bantads.conta_service.dto.DepositoRequestDTO
import com.bantads.conta_service.dto.SaqueRequestDTO
import com.bantads.conta_service.dto.TransferenciaRequestDTO
import com.bantads.conta_service.dto.TransferenciaWriteDTO
import com.bantads.conta_service.entity.comando.TransferenciaWrite
import com.bantads.conta_service.entity.leitura.TransferenciaRead
import com.bantads.conta_service.repository.leitura.ContaRepositoryRead
import com.bantads.conta_service.repository.leitura.TransferenciaRepositoryRead
import com.bantads.conta_service.repository.comando.ContaRepositoryWrite
import com.bantads.conta_service.repository.comando.TransferenciaRepositoryWrite
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import jakarta.transaction.Transactional
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class TransferenciaService(
    private val contaRepositoryRead: ContaRepositoryRead,
    private val transferenciaRepositoryRead: TransferenciaRepositoryRead,
    private val contaRepositoryWrite: ContaRepositoryWrite,
    private val transferenciaRepositoryWrite: TransferenciaRepositoryWrite,
    private val rabbitTemplate: RabbitTemplate
) {

    fun criarTransferenciaRead(transferencia: TransferenciaWriteDTO) {
        transferenciaRepositoryRead.save(
            TransferenciaRead(
                contaOrigem = transferencia.contaOrigem,
                contaOrigemNome = transferencia.contaOrigemNome,
                contaDestino = transferencia.contaDestino,
                contaDestinoNome = transferencia.contaDestinoNome,
                valor = transferencia.valor,
                data = transferencia.data,
                tipo = transferencia.tipo,
                saldoanterior = BigDecimal.ZERO, // O DTO precisaria ter esse campo para ser exato
                saldofinal = transferencia.saldofinal ?: BigDecimal.ZERO
            )
        )
    }

    fun obterSaldo(numero: String): BigDecimal {
        val conta = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")
        return conta.saldo.setScale(2, RoundingMode.HALF_EVEN)
    }

    fun depositar(numero: String, request: DepositoRequestDTO) {
        val valor = request.valor.setScale(2, RoundingMode.HALF_EVEN)
        if (valor <= BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor do depósito deve ser maior que zero")
        }

        val contaRead = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        val saldoAnterior = contaRead.saldo
        val novoSaldo = contaRead.saldo.plus(valor).setScale(2, RoundingMode.HALF_EVEN)

        val contaWrite = contaRepositoryWrite.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")
        contaWrite.saldo = novoSaldo

        val contaSalva = contaRepositoryWrite.save(contaWrite)

        val dataAtual = LocalDateTime.now()

        transferenciaRepositoryWrite.save(
            TransferenciaWrite(
                contaOrigem = contaSalva,
                contaDestino = null,
                valor = valor,
                tipo = "DEPOSITO",
                data = LocalDateTime.now()
            )
        )

        syncContaReadModel(contaSalva)

        val eventoTransferencia = TransferenciaWriteDTO(
            contaOrigem = contaSalva.numero,
            contaOrigemNome = contaSalva.cliente,
            contaDestino = "",
            contaDestinoNome = "",
            tipo = "DEPOSITO",
            valor = valor,
            saldofinal = contaSalva.saldo,
            saldoanterior = saldoAnterior,
            data = dataAtual
        )
        rabbitTemplate.convertAndSend(CQRS_EVENT_EXCHANGE, "cqrs.event.transferencia", eventoTransferencia)
    }

    fun sacar(numero: String, request: SaqueRequestDTO) {
        val valor = request.valor.setScale(2, RoundingMode.HALF_EVEN)
        if (valor <= BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor do saque deve ser maior que zero")
        }

        val contaRead = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        val disponivel = contaRead.saldo.plus(contaRead.limite)
        if (disponivel < valor) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente")
        }

        val saldoAnterior = contaRead.saldo
        val novoSaldo = contaRead.saldo.minus(valor).setScale(2, RoundingMode.HALF_EVEN)

        val contaWrite = contaRepositoryWrite.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")
        contaWrite.saldo = novoSaldo

        val contaSalva = contaRepositoryWrite.save(contaWrite)
        val dataAtual = LocalDateTime.now()

        transferenciaRepositoryWrite.save(
            TransferenciaWrite(
                contaOrigem = contaSalva,
                contaDestino = null,
                valor = valor,
                tipo = "SAQUE",
                data = dataAtual
            )
        )

        syncContaReadModel(contaSalva)

        val eventoTransferencia = TransferenciaWriteDTO(
            contaOrigem = contaSalva.numero,
            contaOrigemNome = contaSalva.cliente,
            contaDestino = "",
            contaDestinoNome = "",
            tipo = "SAQUE",
            valor = valor,
            saldofinal = contaSalva.saldo,
            saldoanterior = saldoAnterior,
            data = dataAtual
        )
        rabbitTemplate.convertAndSend(CQRS_EVENT_EXCHANGE, "cqrs.event.transferencia", eventoTransferencia)
    }

    fun transferir(numero: String, request: TransferenciaRequestDTO) {
        val valor = request.valor.setScale(2, RoundingMode.HALF_EVEN)
        if (valor <= BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor da transferência deve ser maior que zero")
        }

        if (request.contaDestino.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de destino é obrigatória")
        }

        if (numero == request.contaDestino) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta de destino deve ser diferente da conta de origem")
        }

        val contaOrigem = contaRepositoryWrite.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de origem não encontrada")
        val contaDestino = contaRepositoryWrite.findByNumero(request.contaDestino)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de destino não encontrada")

        val disponivel = contaOrigem.saldo.plus(contaOrigem.limite)
        if (disponivel < valor) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para transferência")
        }

        val saldoAnteriorOrigem = contaOrigem.saldo
        contaOrigem.saldo = contaOrigem.saldo.minus(valor).setScale(2, RoundingMode.HALF_EVEN)
        contaDestino.saldo = contaDestino.saldo.plus(valor).setScale(2, RoundingMode.HALF_EVEN)

        val contaOrigemAtualizada = contaRepositoryWrite.save(contaOrigem)
        val contaDestinoAtualizada = contaRepositoryWrite.save(contaDestino)
        val dataAtual = LocalDateTime.now()

        transferenciaRepositoryWrite.save(
            TransferenciaWrite(
                contaOrigem = contaOrigemAtualizada,
                contaDestino = contaDestinoAtualizada,
                valor = valor,
                tipo = "TRANSFERENCIA",
                data = dataAtual
            )
        )

        syncContaReadModel(contaOrigemAtualizada)
        syncContaReadModel(contaDestinoAtualizada)

        val eventoTransferencia = TransferenciaWriteDTO(
            contaOrigem = contaOrigemAtualizada.numero,
            contaOrigemNome = contaOrigemAtualizada.cliente,
            contaDestino = contaDestinoAtualizada.numero,
            contaDestinoNome = contaDestinoAtualizada.cliente,
            tipo = "TRANSFERENCIA",
            valor = valor,
            saldofinal = contaOrigemAtualizada.saldo,
            saldoanterior = saldoAnteriorOrigem,
            data = dataAtual
        )
        rabbitTemplate.convertAndSend(CQRS_EVENT_EXCHANGE, "cqrs.event.transferencia", eventoTransferencia)
    }


    fun obterExtrato(numero: String, dataInicio: String?, dataFim: String?): List<TransferenciaRead> {
        contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        val doOrigem = transferenciaRepositoryRead.findByContaOrigem(numero)
        val doDestino = transferenciaRepositoryRead.findByContaDestino(numero)
        val transferencias = (doOrigem + doDestino).distinctBy { it.id }.sortedBy { it.data }

        return if (!dataInicio.isNullOrBlank() && !dataFim.isNullOrBlank()) {
            val inicio = try {
                LocalDateTime.parse(dataInicio)
            } catch (ex: Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Data de início inválida")
            }
            val fim = try {
                LocalDateTime.parse(dataFim)
            } catch (ex: Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Data de fim inválida")
            }
            if (fim.isBefore(inicio)) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervalo inválido: data de fim anterior à data de início")
            }
            transferencias.filter { it.data in inicio..fim }
        } else {
            transferencias
        }
    }


    private fun syncContaReadModel(conta: com.bantads.conta_service.entity.comando.ContaWrite) {
        val eventoCqrs = ContaWriteDTO(
            cliente = conta.cliente,
            numero = conta.numero,
            saldo = conta.saldo,
            limite = conta.limite,
            gerente = conta.gerente,
            criacao = conta.criacao
        )
        rabbitTemplate.convertAndSend(CQRS_EVENT_EXCHANGE, "cqrs.event.conta", eventoCqrs)
    }
}
