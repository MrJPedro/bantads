package com.bantads.conta_service.service

import com.bantads.conta_service.dto.DepositoRequestDTO
import com.bantads.conta_service.dto.SaqueRequestDTO
import com.bantads.conta_service.dto.TransferenciaRequestDTO
import com.bantads.conta_service.entity.comando.Transferencia
import com.bantads.conta_service.repository.leitura.ContaRepositoryRead
import com.bantads.conta_service.repository.leitura.TransferenciaRepositoryRead
import com.bantads.conta_service.repository.comando.ContaRepositoryWrite
import com.bantads.conta_service.repository.comando.TransferenciaRepositoryWrite
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class TransferenciaService(
    private val contaRepositoryRead: ContaRepositoryRead,
    private val transferenciaRepositoryRead: TransferenciaRepositoryRead,
    private val contaRepositoryWrite: ContaRepositoryWrite,
    private val transferenciaRepositoryWrite: TransferenciaRepositoryWrite
) {

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

        val conta = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        conta.saldo = conta.saldo.plus(valor).setScale(2, RoundingMode.HALF_EVEN)
        val contaSalva = contaRepositoryWrite.save(conta)
        transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaSalva,
                contaDestino = null,
                valor = valor,
                saldofinal = contaSalva.saldo,
                data = LocalDateTime.now()
            )
        )
    }

    fun sacar(numero: String, request: SaqueRequestDTO) {
        val valor = request.valor.setScale(2, RoundingMode.HALF_EVEN)
        if (valor <= BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor do saque deve ser maior que zero")
        }

        val conta = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        val disponivel = conta.saldo.plus(conta.limite)
        if (disponivel < valor) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente")
        }

        conta.saldo = conta.saldo.minus(valor).setScale(2, RoundingMode.HALF_EVEN)
        val contaSalva = contaRepositoryWrite.save(conta)
        transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaSalva,
                contaDestino = null,
                valor = valor,
                saldofinal = contaSalva.saldo,
                data = LocalDateTime.now()
            )
        )
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

        val contaOrigem = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de origem não encontrada")
        val contaDestino = contaRepositoryRead.findByNumero(request.contaDestino)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de destino não encontrada")

        val disponivel = contaOrigem.saldo.plus(contaOrigem.limite)
        if (disponivel < valor) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para transferência")
        }

        contaOrigem.saldo = contaOrigem.saldo.minus(valor).setScale(2, RoundingMode.HALF_EVEN)
        contaDestino.saldo = contaDestino.saldo.plus(valor).setScale(2, RoundingMode.HALF_EVEN)

        contaRepositoryWrite.save(contaOrigem)
        contaRepositoryWrite.save(contaDestino)

        val contaOrigemRefresh = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar conta de origem")
        val contaDestinoRefresh = contaRepositoryRead.findByNumero(request.contaDestino)
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao atualizar conta de destino")

        transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaOrigemRefresh,
                contaDestino = contaDestinoRefresh,
                valor = valor,
                saldofinal = contaOrigemRefresh.saldo,
                data = LocalDateTime.now()
            )
        )
    }

    fun obterExtrato(numero: String, dataInicio: String?, dataFim: String?): List<Transferencia> {
        val conta = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        val transferencias = transferenciaRepositoryRead.findByContaOrigem(conta)

        return if (!dataInicio.isNullOrBlank() && !dataFim.isNullOrBlank()) {
            val inicio = try {
                java.time.LocalDateTime.parse(dataInicio)
            } catch (ex: Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Data de início inválida")
            }
            val fim = try {
                java.time.LocalDateTime.parse(dataFim)
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
    
}