package com.bantads.cliente_service.service

import com.bantads.cliente_service.dto.*
import com.bantads.cliente_service.entity.ClienteEntity
import com.bantads.cliente_service.repository.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ClienteService(
  private val ClienteRepository: ClienteRepository
) {

}
