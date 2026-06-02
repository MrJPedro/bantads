package com.bantads.conta_service.repository.comando

import com.bantads.conta_service.entity.comando.TransferenciaWrite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransferenciaRepositoryWrite : JpaRepository<TransferenciaWrite, Long> {

    fun save(transferencia: TransferenciaWrite): TransferenciaWrite

}