package com.bantads.conta_service.repository

import com.bantads.conta_service.entity.Conta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ContaRepository : JpaRepository<Conta, Long> {
    

    fun findByCliente(cliente: String): List<Conta>
    

    fun findByNumero(numero: String): Conta?
    
}