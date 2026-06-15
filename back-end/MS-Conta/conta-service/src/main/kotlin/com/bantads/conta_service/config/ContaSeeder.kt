package com.bantads.conta_service.config

import com.bantads.conta_service.repository.comando.ContaRepositoryWrite
import com.bantads.conta_service.service.ContaService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration

@Configuration
class ContaSeeder(
    private val contaRepositoryWrite: ContaRepositoryWrite,
    private val contaService: ContaService
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(ContaSeeder::class.java)

    override fun run(vararg args: String) {
        if (contaRepositoryWrite.count() == 0L) {
            logger.info("Banco de dados de Contas vazio. Iniciando seed de Contas e Movimentações pré-cadastradas...")
            contaService.reboot()
        }
    }
}
