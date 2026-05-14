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
        conta.saldo += request.valor
        contaRepositoryWrite.save(conta)
        return Any()
    }

    fun sacar(numero: String, request: SaqueRequestDTO): Any {
        val conta = contaRepositoryRead.findByNumero(numero) ?: return Any()
        if (conta.saldo < request.valor) return Any()
        conta.saldo = conta.saldo - request.valor
        contaRepositoryWrite.save(conta)
        return Any()
    }

    fun transferir(numero: String, request: TransferenciaRequestDTO): Any {
        val contaOrigem = contaRepositoryRead.findByNumero(numero) ?: return Any()
        val contaDestino = contaRepositoryRead.findByNumero(request.contaDestino) ?: return Any()
        if (contaOrigem.saldo < request.valor) return Any()
        contaOrigem.saldo = contaOrigem.saldo - request.valor
        contaDestino.saldo += request.valor
        contaRepositoryWrite.save(contaOrigem)
        contaRepositoryWrite.save(contaDestino) 
        val transferencia = transferenciaRepositoryWrite.save(
            Transferencia(
                contaOrigem = contaOrigem,
                contaDestino = contaDestino,
                valor = request.valor,
                data = LocalDateTime.now(),
                saldofinal =  contaOrigem.saldo
            )
        )
        return Any()
    }

    fun obterExtrato(cpf: String, dataInicio: String?, dataFim: String?): Any {
        // a implementar
        return Any()
    }
    
}