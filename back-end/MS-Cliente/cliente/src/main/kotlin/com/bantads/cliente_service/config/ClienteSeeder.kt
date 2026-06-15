package com.bantads.cliente_service.config

import com.bantads.cliente_service.repository.ClienteRepository
import com.bantads.cliente_service.service.ClienteService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration

@Configuration
class ClienteSeeder(
    private val clienteRepository: ClienteRepository,
    private val clienteService: ClienteService
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(ClienteSeeder::class.java)

    override fun run(vararg args: String) {
        if (clienteRepository.count() == 0L) {
            logger.info("Banco de dados de Clientes vazio. Iniciando seed de Clientes pré-cadastrados...")
            clienteService.reboot()
        }
    }
}
