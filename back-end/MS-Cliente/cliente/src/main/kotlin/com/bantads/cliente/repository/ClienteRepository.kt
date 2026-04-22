package com.bantads.cliente.repository

import com.bantads.cliente.entity.ClienteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ClienteRepository : JpaRepository<ClienteEntity, Long> {
}