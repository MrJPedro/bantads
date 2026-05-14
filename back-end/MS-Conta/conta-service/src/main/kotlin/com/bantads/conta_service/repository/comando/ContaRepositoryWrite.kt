package com.bantads.conta_service.repository.comando

import com.bantads.conta_service.entity.comando.Conta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContaRepositoryWrite: JpaRepository<Conta, Long> {

    fun save(conta: Conta): Conta
    
}