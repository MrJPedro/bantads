package com.bantads.conta_service.repository.comando

import com.bantads.conta_service.entity.comando.Transferencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransferenciaRepositoryWrite : JpaRepository<Transferencia, Long> {

    fun save(transferencia: Transferencia): Transferencia

}