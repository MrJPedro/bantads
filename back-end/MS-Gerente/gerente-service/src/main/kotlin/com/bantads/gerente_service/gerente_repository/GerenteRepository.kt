package com.bantads.gerente_service.gerente_repository

import com.bantads.gerente_service.model.Gerente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GerenteRepository : JpaRepository<Gerente, Long> {

    fun findByCpf(cpf: String): Gerente?
}