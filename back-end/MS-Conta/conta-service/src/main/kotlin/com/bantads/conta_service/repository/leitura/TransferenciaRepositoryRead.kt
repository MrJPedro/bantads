package com.bantads.conta_service.repository.leitura

import com.bantads.conta_service.entity.comando.Conta
import com.bantads.conta_service.entity.comando.Transferencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransferenciaRepositoryRead : JpaRepository<Transferencia, Long> {

    fun findByContaOrigem(contaOrigem: Conta): List<Transferencia>

    fun findByContaDestino(contaDestino: Conta): List<Transferencia>

}