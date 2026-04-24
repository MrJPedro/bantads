package com.bantads.gerente_service.repository

import com.bantads.gerente_service.entity.GerenteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GerenteRepository : JpaRepository<GerenteEntity, Long> {
    
    fun findByCpf(cpf: String): GerenteEntity?
    
    fun findTopByOrderByQuantidadeClientesAsc(): GerenteEntity?
    
    fun findTopByOrderByQuantidadeClientesDesc(): GerenteEntity?
}