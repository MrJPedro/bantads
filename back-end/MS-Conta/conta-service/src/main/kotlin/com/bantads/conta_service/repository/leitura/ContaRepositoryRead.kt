package com.bantads.conta_service.repository.leitura

import com.bantads.conta_service.entity.leitura.ContaRead
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContaRepositoryRead : JpaRepository<ContaRead, Long> {
    fun findByGerente(gerente: String): List<ContaRead>

    fun findFirstByCliente(cliente: String): ContaRead?

    fun findByNumero(numero: String): ContaRead?

    fun findTop3ByOrderBySaldoDesc(): List<ContaRead>
}
