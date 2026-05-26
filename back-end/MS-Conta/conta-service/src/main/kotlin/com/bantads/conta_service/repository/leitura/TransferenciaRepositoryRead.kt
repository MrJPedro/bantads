package com.bantads.conta_service.repository.leitura

import com.bantads.conta_service.entity.leitura.Transferencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransferenciaRepositoryRead : JpaRepository<Transferencia, Long> {

    fun findByContaOrigem(contaOrigem: String): List<Transferencia>

    fun findByContaDestino(contaDestino: String): List<Transferencia>

    fun save(transferencia: com.bantads.conta_service.entity.comando.Transferencia): Transferencia
}