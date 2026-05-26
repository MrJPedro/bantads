package com.bantads.conta_service.service

import com.bantads.conta_service.dto.DepositoRequestDTO
import com.bantads.conta_service.dto.SaqueRequestDTO
import com.bantads.conta_service.dto.TransferenciaRequestDTO
import com.bantads.conta_service.entity.comando.Transferencia
import com.bantads.conta_service.entity.leitura.Transferencia as TransferenciaLeitura
import com.bantads.conta_service.entity.leitura.Conta as ContaLeitura
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

        val contaRead = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        val novoSaldo = contaRead.saldo.plus(valor).setScale(2, RoundingMode.HALF_EVEN)

        val contaWrite = contaRepositoryWrite.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")
        contaWrite.saldo = novoSaldo
        val contaSalva = contaRepositoryWrite.save(contaWrite)

        contaRepositoryRead.save(
            ContaLeitura(
                cliente = contaSalva.cliente,
                numero = contaSalva.numero,
                saldo = contaSalva.saldo,
                limite = contaSalva.limite,
                gerente = contaSalva.gerente,
                criacao = contaSalva.criacao
            )
        )

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

        val contaRead = contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        val disponivel = contaRead.saldo.plus(contaRead.limite)
        if (disponivel < valor) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente")
        }

        val novoSaldo = contaRead.saldo.minus(valor).setScale(2, RoundingMode.HALF_EVEN)

        val contaWrite = contaRepositoryWrite.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")
        contaWrite.saldo = novoSaldo
        val contaSalva = contaRepositoryWrite.save(contaWrite)


        contaRepositoryRead.save(
            ContaLeitura(
                cliente = contaSalva.cliente,
                numero = contaSalva.numero,
                saldo = contaSalva.saldo,
                limite = contaSalva.limite,
                gerente = contaSalva.gerente,
                criacao = contaSalva.criacao
            )
        )

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

        val contaOrigem = contaRepositoryWrite.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de origem não encontrada")
        val contaDestino = contaRepositoryWrite.findByNumero(request.contaDestino)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de destino não encontrada")

        val disponivel = contaOrigem.saldo.plus(contaOrigem.limite)
        if (disponivel < valor) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para transferência")
        }

        contaOrigem.saldo = contaOrigem.saldo.minus(valor).setScale(2, RoundingMode.HALF_EVEN)
        contaDestino.saldo = contaDestino.saldo.plus(valor).setScale(2, RoundingMode.HALF_EVEN)

        val contaOrigemAtualizada = contaRepositoryWrite.save(contaOrigem)
        val contaDestinoAtualizada = contaRepositoryWrite.save(contaDestino)

        contaRepositoryRead.save(
            ContaLeitura(
                cliente = contaOrigemAtualizada.cliente,
                numero = contaOrigemAtualizada.numero,
                saldo = contaOrigemAtualizada.saldo,
                limite = contaOrigemAtualizada.limite,
                gerente = contaOrigemAtualizada.gerente,
                criacao = contaOrigemAtualizada.criacao
            )
        )

        contaRepositoryRead.save(
            ContaLeitura(
                cliente = contaDestinoAtualizada.cliente,
                numero = contaDestinoAtualizada.numero,
                saldo = contaDestinoAtualizada.saldo,
                limite = contaDestinoAtualizada.limite,
                gerente = contaDestinoAtualizada.gerente,
                criacao = contaDestinoAtualizada.criacao
            )
        )

        transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaOrigemAtualizada,
                contaDestino = contaDestinoAtualizada,
                valor = valor,
                saldofinal = contaOrigemAtualizada.saldo,
                data = LocalDateTime.now()
            )
        )
        transferenciaRepositoryRead.save(
            TransferenciaLeitura(
                contaOrigem = contaOrigemAtualizada.numero,
                contaDestino = contaDestinoAtualizada.numero,
                contaDestinoNome = contaDestinoAtualizada.cliente,
                valor = valor,
                data = LocalDateTime.now(),
                saldofinal = contaOrigemAtualizada.saldo
            )
        )
    }


    fun obterExtrato(numero: String, dataInicio: String?, dataFim: String?): List<TransferenciaLeitura> {
        contaRepositoryRead.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        val transferencias = transferenciaRepositoryRead.findByContaOrigem(numero)

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


    private fun syncContaReadModel(conta: com.bantads.conta_service.entity.comando.Conta) {
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
}