package com.bantads.conta_service.repository.leitura

import com.bantads.conta_service.entity.leitura.TransferenciaRead
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransferenciaRepositoryRead : JpaRepository<TransferenciaRead, Long> {

    fun findByContaOrigem(contaOrigem: String): List<TransferenciaRead>

    fun findByContaDestino(contaDestino: String): List<TransferenciaRead>

    fun save(transferencia: com.bantads.conta_service.entity.comando.TransferenciaWrite): TransferenciaRead
}