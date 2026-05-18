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
import org.springframework.stereotype.Service

@Service
@Transactional
class TransferenciaService(
    private val contaRepositoryRead: ContaRepositoryRead,
    private val transferenciaRepositoryRead: TransferenciaRepositoryRead,
    private val contaRepositoryWrite: ContaRepositoryWrite,
    private val transferenciaRepositoryWrite: TransferenciaRepositoryWrite
) {

    fun obterSaldo(numero: String): BigDecimal {
        val conta = contaRepositoryRead.findByNumero(numero) ?: return BigDecimal.ZERO
        return conta.saldo
    }

    fun depositar(numero: String, request: DepositoRequestDTO): Any {
        val valor = request.valor.setScale(2, RoundingMode.HALF_EVEN)
        val conta = contaRepositoryRead.findByNumero(numero) ?: return Any()
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
        return Any()
    }

    fun sacar(numero: String, request: SaqueRequestDTO): Any {
        val valor = request.valor.setScale(2, RoundingMode.HALF_EVEN)
        val conta = contaRepositoryRead.findByNumero(numero) ?: return Any()
        val disponivel = conta.saldo.plus(conta.limite)
        if (disponivel < valor) return Any()
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
        return Any()
    }

    fun transferir(numero: String, request: TransferenciaRequestDTO): Any {
        val valor = request.valor.setScale(2, RoundingMode.HALF_EVEN)
        val contaOrigem = contaRepositoryRead.findByNumero(numero) ?: return Any()
        val contaDestino = contaRepositoryRead.findByNumero(request.contaDestino) ?: return Any()
        val disponivel = contaOrigem.saldo.plus(contaOrigem.limite)
        if (disponivel < valor) return Any()

        contaOrigem.saldo = contaOrigem.saldo.minus(valor).setScale(2, RoundingMode.HALF_EVEN)
        contaDestino.saldo = contaDestino.saldo.plus(valor).setScale(2, RoundingMode.HALF_EVEN)

        contaRepositoryWrite.save(contaOrigem)
        contaRepositoryWrite.save(contaDestino)
        
        // Refetch entities para evitar problemas com desanexação
        val contaOrigemRefresh = contaRepositoryRead.findByNumero(numero) ?: return Any()
        val contaDestinoRefresh = contaRepositoryRead.findByNumero(request.contaDestino) ?: return Any()
        
        transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaOrigemRefresh,
                contaDestino = contaDestinoRefresh,
                valor = valor,
                saldofinal = contaOrigemRefresh.saldo,
                data = LocalDateTime.now()
            )
        )
        return Any()
    }

    fun obterExtrato(numero: String, dataInicio: String?, dataFim: String?): List<Transferencia> {
        val conta = contaRepositoryRead.findByNumero(numero) ?: return emptyList()
        val transferencias = transferenciaRepositoryRead.findByContaOrigem(conta)
        
        return if (dataInicio != null && dataFim != null) {
            val inicio = java.time.LocalDateTime.parse(dataInicio)
            val fim = java.time.LocalDateTime.parse(dataFim)
            transferencias.filter { it.data in inicio..fim }
        } else {
            transferencias
        }
    }
    
}