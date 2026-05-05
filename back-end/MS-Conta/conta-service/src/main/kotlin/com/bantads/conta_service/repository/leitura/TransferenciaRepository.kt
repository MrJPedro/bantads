package com.bantads.conta_service.repository

import com.bantads.conta_service.entity.Transferencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransferenciaRepository : JpaRepository<Transferencia, Long> {

    fun findByCliente(cliente: String): List<Transferencia>

    fun findByNumero(numero: String): List<Transferencia>
}