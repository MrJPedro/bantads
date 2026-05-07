package com.bantads.conta_service.service

import com.bantads.conta_service.dto.criarContaDTO
import com.bantads.conta_service.entity.Conta
import com.bantads.conta_service.repository.comando.ContaRepositoryWrite
import com.bantads.conta_service.repository.comando.TransferenciaRepositoryWrite
import com.bantads.conta_service.repository.leitura.ContaRepositoryRead
import com.bantads.conta_service.repository.leitura.TransferenciaRepositoryRead
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class ContaService(
    private val contaRepositoryRead: ContaRepositoryRead,
    private val transferenciaRepositoryRead: TransferenciaRepositoryRead,
    private val contaRepositoryWrite: ContaRepositoryWrite,
    private val transferenciaRepositoryWrite: TransferenciaRepositoryWrite
) {
        fun criar(numero : String, request : criarContaDTO): Any
        {
            val conta = contaRepositoryWrite.save(
                com.bantads.conta_service.entity.Conta(
                    cliente = request.cliente,
                    numero = numero,
                    saldo = request.saldo,
                    limite = request.limite,
                    gerente = request.gerente,
                    criacao = LocalDateTime.now()
                )
            )

            return Any();
        }
}