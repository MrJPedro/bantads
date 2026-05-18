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
        val conta = contaRepositoryRead.findByNumero(numero) ?: return Any()
        conta.saldo = conta.saldo.plus(request.valor)
        val contaSalva = contaRepositoryWrite.save(conta)
        transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaSalva,
                contaDestino = null,
                valor = request.valor,
                saldofinal = contaSalva.saldo,
                data = LocalDateTime.now()
            )
        )
        return Any()
    }

    fun sacar(numero: String, request: SaqueRequestDTO): Any {
        val conta = contaRepositoryRead.findByNumero(numero) ?: return Any()
        if (conta.saldo < request.valor) return Any()
        conta.saldo = conta.saldo.minus(request.valor)
        val contaSalva = contaRepositoryWrite.save(conta)
        transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaSalva,
                contaDestino = null,
                valor = request.valor,
                saldofinal = contaSalva.saldo,
                data = LocalDateTime.now()
            )
        )
        return Any()
    }

    fun transferir(numero: String, request: TransferenciaRequestDTO): Any {
        val contaOrigem = contaRepositoryRead.findByNumero(numero) ?: return Any()
        val contaDestino = contaRepositoryRead.findByNumero(request.contaDestino) ?: return Any()
        if (contaOrigem.saldo < request.valor) return Any()
        
        contaOrigem.saldo = contaOrigem.saldo.minus(request.valor)
        contaDestino.saldo = contaDestino.saldo.plus(request.valor)
        
        contaRepositoryWrite.save(contaOrigem)
        contaRepositoryWrite.save(contaDestino)
        
        // Refetch entities para evitar problemas com desanexação
        val contaOrigemRefresh = contaRepositoryRead.findByNumero(numero) ?: return Any()
        val contaDestinoRefresh = contaRepositoryRead.findByNumero(request.contaDestino) ?: return Any()
        
        transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaOrigemRefresh,
                contaDestino = contaDestinoRefresh,
                valor = request.valor,
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