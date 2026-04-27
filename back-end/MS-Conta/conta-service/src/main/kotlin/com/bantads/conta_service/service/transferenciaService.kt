package com.bantads.conta_service.service

import com.bantads.conta_service.dtos.DepositoRequestDTO
import com.bantads.conta_service.dtos.SaqueRequestDTO
import com.bantads.conta_service.dtos.TransferenciaRequestDTO
import com.bantads.conta_service.repository.ContaRepository
import com.bantads.conta_service.repository.TransferenciaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class TransferenciaService(
    private val contaRepository: ContaRepository,
    private val transferenciaRepository: TransferenciaRepository
) {

    fun obterSaldo(cpf: String): Any {
        // a implementar
        return Any()
    }

    fun depositar(cpf: String, request: DepositoRequestDTO): Any {
        // a implementar
        return Any()
    }

    fun sacar(cpf: String, request: SaqueRequestDTO): Any {
        // a implementar
        return Any()
    }

    fun transferir(cpf: String, request: TransferenciaRequestDTO): Any {
        // a implementar
        return Any()
    }

    fun obterExtrato(cpf: String, dataInicio: String?, dataFim: String?): Any {
        // a implementar
        return Any()
    }
    