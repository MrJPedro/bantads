package com.bantads.conta_service.repository.leitura

import com.bantads.conta_service.entity.Conta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContaRepositoryRead : JpaRepository<Conta, Long> {
    

    fun findByCliente(cliente: String): List<Conta>
    

    fun findByNumero(numero: String): Conta?
    
}