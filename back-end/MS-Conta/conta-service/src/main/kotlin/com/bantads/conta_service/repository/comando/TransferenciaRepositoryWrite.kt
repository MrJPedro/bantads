package com.bantads.conta_service.repository

import com.bantads.conta_service.entity.Transferencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransferenciaRepositoryWrite : JpaRepository<Transferencia, Long> {

    fun save(transferencia: Transferencia): Transferencia

}