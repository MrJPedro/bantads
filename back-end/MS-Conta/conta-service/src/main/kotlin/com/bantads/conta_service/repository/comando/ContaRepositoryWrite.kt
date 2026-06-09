package com.bantads.conta_service.repository.comando

import com.bantads.conta_service.entity.comando.ContaWrite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContaRepositoryWrite: JpaRepository<ContaWrite, Long> {

    fun save(conta: ContaWrite): ContaWrite

    fun findByNumero(numero: String): ContaWrite?
}
