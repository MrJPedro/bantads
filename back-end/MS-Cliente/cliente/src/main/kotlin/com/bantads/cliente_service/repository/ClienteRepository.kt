package com.bantads.cliente_service.repository

import com.bantads.cliente_service.entity.ClienteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ClienteRepository : JpaRepository<ClienteEntity, Long> {
}
