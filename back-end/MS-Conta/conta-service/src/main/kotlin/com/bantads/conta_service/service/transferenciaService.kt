package com.bantads.conta_service.service

import com.bantads.conta_service.dto.DepositoRequestDTO
import com.bantads.conta_service.dto.SaqueRequestDTO
import com.bantads.conta_service.dto.TransferenciaRequestDTO
import com.bantads.conta_service.repository.ContaRepository
import com.bantads.conta_service.repository.TransferenciaRepository
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

    fun obterSaldo(cpf: String): BigDecimal {
        val conta = contaRepositoryRead.findByCliente(cpf).firstOrNull() ?: return BigDecimal.ZERO
        return conta.saldo
    }

    fun depositar(cpf: String, request: DepositoRequestDTO): Any {
        val conta = contaRepositoryRead.findByCliente(cpf).firstOrNull() ?: return Any()
        conta.saldo += request.valor
        contaRepositoryWrite.save(conta)
        return Any()
    }

    fun sacar(cpf: String, request: SaqueRequestDTO): Any {
        val conta = contaRepositoryRead.findByCliente(cpf).firstOrNull() ?: return Any()
        if (conta.saldo < request.valor) return Any()
        conta.saldo = conta.saldo - request.valor
        contaRepositoryWrite.save(conta)
        return Any()
    }

    fun transferir(cpf: String, request: TransferenciaRequestDTO): Any {
        val contaOrigem = contaRepositoryRead.findByCliente(cpf).firstOrNull() ?: return Any()
        val contaDestino = contaRepositoryRead.findByCliente(request.contaDestino).firstOrNull() ?: return Any()
        if (contaOrigem.saldo < request.valor) return Any()
        contaOrigem.saldo = contaOrigem.saldo - request.valor
        contaDestino.saldo += request.valor
        contaRepositoryWrite.save(contaOrigem)
        contaRepositoryWrite.save(contaDestino) 
        val transferencia = transferenciaRepositoryWrite.save(
            com.bantads.conta_service.entity.Transferencia(
                contaOrigem = contaOrigem,
                contaDestino = contaDestino,
                valor = request.valor,
                data = LocalDateTime.now() 
            )
        )
        return Any()
    }

    fun obterExtrato(cpf: String, dataInicio: String?, dataFim: String?): Any {
        // a implementar
        return Any()
    }
    
}