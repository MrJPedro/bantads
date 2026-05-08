package com.bantads.gerente_service.config

import com.bantads.gerente_service.entity.GerenteEntity
import com.bantads.gerente_service.repository.GerenteRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional

@Configuration
class GerenteSeeder(
    private val gerenteRepository: GerenteRepository
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(GerenteSeeder::class.java)

    @Transactional
    override fun run(vararg args: String) { 
        // Verifica se o banco está vazio
        if (gerenteRepository.count() == 0L) {
            logger.info("Banco de dados vazio. Iniciando seed de Gerentes pré-cadastrados")

            val gerentesIniciais = listOf(
                GerenteEntity(
                    cpf = "98574307084",
                    nome = "Geniéve",
                    email = "ger1@bantads.com.br",
                    telefone = "(41) 98765-4321",
                    quantidadeClientes = 2
                ),
                GerenteEntity(
                    cpf = "64065268052",
                    nome = "Godophredo",
                    email = "ger2@bantads.com.br",
                    telefone = "(11) 89765-1234",
                    quantidadeClientes = 2
                ),
                GerenteEntity(
                    cpf = "23862179060",
                    nome = "Gyândula",
                    email = "ger3@bantads.com.br",
                    telefone = "(42) 98888-9999",
                    quantidadeClientes = 1
                )
            )

            gerenteRepository.saveAll(gerentesIniciais)
            logger.info("Seed de Gerentes finalizado com sucesso! ${gerentesIniciais.size} gerentes inseridos.")
        } else {
            logger.info("Banco de dados de Gerentes já populado. Seed ignorado.")
        }
    }
}