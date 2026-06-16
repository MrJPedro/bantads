package com.bantads.conta_service.service

import com.bantads.conta_service.dto.ContaDTO
import com.bantads.conta_service.dto.ContaDetalhesDTO
import com.bantads.conta_service.dto.ContaWriteDTO
import com.bantads.conta_service.entity.comando.ContaWrite
import com.bantads.conta_service.entity.leitura.ContaRead as ContaLeitura
import com.bantads.conta_service.repository.comando.ContaRepositoryWrite
import com.bantads.conta_service.repository.leitura.ContaRepositoryRead
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random
import org.springframework.amqp.rabbit.core.RabbitTemplate
import com.bantads.conta_service.config.CQRS_EVENT_EXCHANGE
import com.bantads.conta_service.entity.comando.TransferenciaWrite
import com.bantads.conta_service.entity.leitura.TransferenciaRead
import com.bantads.conta_service.repository.comando.TransferenciaRepositoryWrite
import com.bantads.conta_service.repository.leitura.TransferenciaRepositoryRead
import java.time.LocalDateTime

@Service
@Transactional
class ContaService(
    private val contaRepositoryRead: ContaRepositoryRead,
    private val contaRepositoryWrite: ContaRepositoryWrite,
    private val transferenciaRepositoryRead: TransferenciaRepositoryRead,
    private val transferenciaRepositoryWrite: TransferenciaRepositoryWrite,
    private val rabbitTemplate: RabbitTemplate
) {

    fun criar(numero: String, request: ContaDTO): Any {
        if (request.cliente.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente é obrigatório")
        }

        if (request.saldo < BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo inicial não pode ser negativo")
        }

        if (request.limite < BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Limite não pode ser negativo")
        }

        if (contaRepositoryRead.findByNumero(numero) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Número de conta já existe")
        }

        val conta = contaRepositoryWrite.save(
            ContaWrite(
                cliente = request.cliente,
                numero = numero,
                saldo = request.saldo.setScale(2, RoundingMode.HALF_EVEN),
                limite = request.limite.setScale(2, RoundingMode.HALF_EVEN),
                gerente = request.gerente,
                criacao = request.criacao
            )
        )

        val eventoCqrs = ContaWriteDTO(
            cliente = request.cliente,
            numero = numero,
            saldo = request.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = request.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = request.gerente,
            criacao = request.criacao
        )
        rabbitTemplate.convertAndSend(CQRS_EVENT_EXCHANGE, "cqrs.event.conta", eventoCqrs)

        return conta
    }

    // apenas para uso do CQRS
    fun criarContaRead(conta: ContaWriteDTO)
    {
        contaRepositoryRead.save(
            ContaLeitura(
                cliente = conta.cliente,
                numero = conta.numero,
                saldo = conta.saldo,
                limite = conta.limite,
                gerente = conta.gerente,
                criacao = conta.criacao
            )
        )
    }

    fun atualizarGerente(numero: String, gerenteCpf: String): ContaDetalhesDTO {
        if (gerenteCpf.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF do gerente é obrigatório")
        }

        val conta = contaRepositoryWrite.findByNumero(numero)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")

        conta.gerente = gerenteCpf
        val contaAtualizada = contaRepositoryWrite.save(conta)

        val eventoCqrs = ContaWriteDTO(
            cliente = contaAtualizada.cliente,
            numero = contaAtualizada.numero,
            saldo = contaAtualizada.saldo,
            limite = contaAtualizada.limite,
            gerente = contaAtualizada.gerente,
            criacao = contaAtualizada.criacao
        )
        rabbitTemplate.convertAndSend(CQRS_EVENT_EXCHANGE, "cqrs.event.conta", eventoCqrs)

        return ContaDetalhesDTO(
            cliente = contaAtualizada.cliente,
            numero = contaAtualizada.numero,
            saldo = contaAtualizada.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = contaAtualizada.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = contaAtualizada.gerente,
            criacao = contaAtualizada.criacao.toString()
        )
    }

    fun atualizarLimitePorCliente(cliente: String, salario: BigDecimal): ContaDetalhesDTO? {
        val conta = contaRepositoryWrite.findFirstByCliente(cliente) ?: return null

        val limiteCalculado = calcularLimite(salario)
        val saldoNegativo = if (conta.saldo < BigDecimal.ZERO) {
            conta.saldo.abs().setScale(2, RoundingMode.HALF_EVEN)
        } else {
            BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN)
        }

        conta.limite = if (limiteCalculado < saldoNegativo) {
            saldoNegativo
        } else {
            limiteCalculado
        }.setScale(2, RoundingMode.HALF_EVEN)

        val contaAtualizada = contaRepositoryWrite.save(conta)

        val eventoCqrs = ContaWriteDTO(
            cliente = contaAtualizada.cliente,
            numero = contaAtualizada.numero,
            saldo = contaAtualizada.saldo,
            limite = contaAtualizada.limite,
            gerente = contaAtualizada.gerente,
            criacao = contaAtualizada.criacao
        )
        rabbitTemplate.convertAndSend(CQRS_EVENT_EXCHANGE, "cqrs.event.conta", eventoCqrs)

        return ContaDetalhesDTO(
            cliente = contaAtualizada.cliente,
            numero = contaAtualizada.numero,
            saldo = contaAtualizada.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = contaAtualizada.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = contaAtualizada.gerente,
            criacao = contaAtualizada.criacao.toString()
        )
    }

    fun gerarNumeroContaUnico(): String {
        var numero: String
        do {
            numero = Random.nextInt(1000, 10000).toString()
        } while (contaRepositoryRead.findByNumero(numero) != null)
        return numero
    }

    fun calcularLimite(salario: BigDecimal): BigDecimal {
        val metadeSalario = salario.setScale(2, RoundingMode.HALF_EVEN).divide(BigDecimal(2), RoundingMode.HALF_EVEN)
        return if (salario >= BigDecimal(2000).setScale(2, RoundingMode.HALF_EVEN)) {
            metadeSalario
        } else {
            0.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
        }
    }

    fun obterContaPorCliente(cliente: String): ContaDetalhesDTO? {
        val conta = contaRepositoryRead.findFirstByCliente(cliente) ?: return null
        return ContaDetalhesDTO(
            cliente = conta.cliente,
            numero = conta.numero,
            saldo = conta.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = conta.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = conta.gerente,
            criacao = conta.criacao.toString()
        )
    }

    fun obterContaPorNumero(numero: String): ContaDetalhesDTO? {
        val conta = contaRepositoryRead.findByNumero(numero) ?: return null
        return ContaDetalhesDTO(
            cliente = conta.cliente,
            numero = conta.numero,
            saldo = conta.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = conta.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = conta.gerente,
            criacao = conta.criacao.toString()
        )
    }

    fun obterContasPorGerente(cpfGerente: String): List<ContaDetalhesDTO> {
        return contaRepositoryRead.findByGerente(cpfGerente)
            .map { conta ->
                ContaDetalhesDTO(
                    cliente = conta.cliente,
                    numero = conta.numero,
                    saldo = conta.saldo.setScale(2, RoundingMode.HALF_EVEN),
                    limite = conta.limite.setScale(2, RoundingMode.HALF_EVEN),
                    gerente = conta.gerente,
                    criacao = conta.criacao.toString()
                )
            }
    }

    fun obterTop3Contas(): List<ContaDetalhesDTO> {
        return contaRepositoryRead.findTop3ByOrderBySaldoDesc()
            .map { conta ->
                ContaDetalhesDTO(
                    cliente = conta.cliente,
                    numero = conta.numero,
                    saldo = conta.saldo.setScale(2, RoundingMode.HALF_EVEN),
                    limite = conta.limite.setScale(2, RoundingMode.HALF_EVEN),
                    gerente = conta.gerente,
                    criacao = conta.criacao.toString()
                )
    }
    }

    data class TxSeed(
        val dateTime: LocalDateTime,
        val tipo: String,
        val origin: Pair<String, String>,
        val destination: Pair<String?, String?>
    )

    @Transactional
    fun reboot() {
        transferenciaRepositoryWrite.deleteAll()
        transferenciaRepositoryRead.deleteAll()
        contaRepositoryWrite.deleteAll()
        contaRepositoryRead.deleteAll()

        transferenciaRepositoryWrite.flush()
        transferenciaRepositoryRead.flush()
        contaRepositoryWrite.flush()
        contaRepositoryRead.flush()

        val accountsWrite = listOf(
            ContaWrite(
                cliente = "12912861012",
                numero = "1291",
                saldo = BigDecimal("800.00"),
                limite = BigDecimal("5000.00"),
                gerente = "98574307084",
                criacao = LocalDateTime.of(2000, 1, 1, 0, 0)
            ),
            ContaWrite(
                cliente = "09506382000",
                numero = "0950",
                saldo = BigDecimal("-10000.00"),
                limite = BigDecimal("10000.00"),
                gerente = "64065268052",
                criacao = LocalDateTime.of(1990, 10, 10, 0, 0)
            ),
            ContaWrite(
                cliente = "85733854057",
                numero = "8573",
                saldo = BigDecimal("-1000.00"),
                limite = BigDecimal("1500.00"),
                gerente = "23862179060",
                criacao = LocalDateTime.of(2012, 12, 12, 0, 0)
            ),
            ContaWrite(
                cliente = "58872160006",
                numero = "5887",
                saldo = BigDecimal("150000.00"),
                limite = BigDecimal("0.00"),
                gerente = "98574307084",
                criacao = LocalDateTime.of(2022, 2, 22, 0, 0)
            ),
            ContaWrite(
                cliente = "76179646090",
                numero = "7617",
                saldo = BigDecimal("1500.00"),
                limite = BigDecimal("0.00"),
                gerente = "64065268052",
                criacao = LocalDateTime.of(2025, 1, 1, 0, 0)
            )
        )
        val savedAccounts = contaRepositoryWrite.saveAll(accountsWrite).associateBy { it.numero }

        val accountsRead = listOf(
            ContaLeitura(
                numero = "1291",
                cliente = "12912861012",
                saldo = BigDecimal("800.00"),
                limite = BigDecimal("5000.00"),
                gerente = "98574307084",
                criacao = LocalDateTime.of(2000, 1, 1, 0, 0)
            ),
            ContaLeitura(
                numero = "0950",
                cliente = "09506382000",
                saldo = BigDecimal("-10000.00"),
                limite = BigDecimal("10000.00"),
                gerente = "64065268052",
                criacao = LocalDateTime.of(1990, 10, 10, 0, 0)
            ),
            ContaLeitura(
                numero = "8573",
                cliente = "85733854057",
                saldo = BigDecimal("-1000.00"),
                limite = BigDecimal("1500.00"),
                gerente = "23862179060",
                criacao = LocalDateTime.of(2012, 12, 12, 0, 0)
            ),
            ContaLeitura(
                numero = "5887",
                cliente = "58872160006",
                saldo = BigDecimal("150000.00"),
                limite = BigDecimal("0.00"),
                gerente = "98574307084",
                criacao = LocalDateTime.of(2022, 2, 22, 0, 0)
            ),
            ContaLeitura(
                numero = "7617",
                cliente = "76179646090",
                saldo = BigDecimal("1500.00"),
                limite = BigDecimal("0.00"),
                gerente = "64065268052",
                criacao = LocalDateTime.of(2025, 1, 1, 0, 0)
            )
        )
        contaRepositoryRead.saveAll(accountsRead)

        val txs = listOf(
            TxSeed(LocalDateTime.of(2020, 1, 1, 10, 0), "DEPOSITO", Pair("1291", "Catharyna"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2020, 1, 1, 11, 0), "DEPOSITO", Pair("1291", "Catharyna"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2020, 1, 1, 12, 0), "SAQUE", Pair("1291", "Catharyna"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2020, 1, 1, 13, 0), "SAQUE", Pair("1291", "Catharyna"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2020, 1, 10, 15, 0), "DEPOSITO", Pair("1291", "Catharyna"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2020, 1, 15, 8, 0), "SAQUE", Pair("1291", "Catharyna"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2020, 1, 20, 12, 0), "TRANSFERENCIA", Pair("1291", "Catharyna"), Pair("0950", "Cleuddônio")),
            TxSeed(LocalDateTime.of(2025, 1, 1, 12, 0), "DEPOSITO", Pair("0950", "Cleuddônio"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2025, 1, 2, 10, 0), "DEPOSITO", Pair("0950", "Cleuddônio"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2025, 1, 10, 10, 0), "SAQUE", Pair("0950", "Cleuddônio"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2025, 2, 5, 10, 0), "DEPOSITO", Pair("0950", "Cleuddônio"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2025, 5, 5, 0, 0), "DEPOSITO", Pair("8573", "Catianna"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2025, 5, 6, 0, 0), "SAQUE", Pair("8573", "Catianna"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2025, 6, 1, 0, 0), "DEPOSITO", Pair("5887", "Cutardo"), Pair(null, null)),
            TxSeed(LocalDateTime.of(2025, 7, 1, 0, 0), "DEPOSITO", Pair("7617", "Coândrya"), Pair(null, null))
        )

        val values = listOf(
            BigDecimal("1000.00"),
            BigDecimal("900.00"),
            BigDecimal("550.00"),
            BigDecimal("350.00"),
            BigDecimal("2000.00"),
            BigDecimal("500.00"),
            BigDecimal("1700.00"),
            BigDecimal("1000.00"),
            BigDecimal("5000.00"),
            BigDecimal("200.00"),
            BigDecimal("7000.00"),
            BigDecimal("1000.00"),
            BigDecimal("2000.00"),
            BigDecimal("150000.00"),
            BigDecimal("1500.00")
        )

        val readSaldosOrigemAnterior = listOf(
            BigDecimal("0.00"), 
            BigDecimal("1000.00"), 
            BigDecimal("1900.00"), 
            BigDecimal("1350.00"), 
            BigDecimal("1000.00"), 
            BigDecimal("3000.00"), 
            BigDecimal("2500.00"), 
            BigDecimal("1700.00"), 
            BigDecimal("2700.00"), 
            BigDecimal("7700.00"), 
            BigDecimal("7500.00"), 
            BigDecimal("0.00"), 
            BigDecimal("1000.00"), 
            BigDecimal("0.00"), 
            BigDecimal("0.00") 
        )

        val readSaldosOrigemFinal = listOf(
            BigDecimal("1000.00"),
            BigDecimal("1900.00"),
            BigDecimal("1350.00"),
            BigDecimal("1000.00"),
            BigDecimal("3000.00"),
            BigDecimal("2500.00"),
            BigDecimal("800.00"),
            BigDecimal("2700.00"),
            BigDecimal("7700.00"),
            BigDecimal("7500.00"),
            BigDecimal("14500.00"),
            BigDecimal("1000.00"),
            BigDecimal("-1000.00"),
            BigDecimal("150000.00"),
            BigDecimal("1500.00")
        )

        for (i in txs.indices) {
            val tx = txs[i]
            val valUnit = values[i]
            val date = tx.dateTime
            val tipo = tx.tipo
            val orgPair = tx.origin
            val destPair = tx.destination

            val contaOrigem = savedAccounts[orgPair.first]!!
            val contaDestino = if (destPair.first != null) savedAccounts[destPair.first!!] else null

            transferenciaRepositoryWrite.save(
                TransferenciaWrite(
                    contaOrigem = contaOrigem,
                    contaDestino = contaDestino,
                    valor = valUnit,
                    tipo = tipo,
                    data = date
                )
            )

            transferenciaRepositoryRead.save(
                TransferenciaRead(
                    contaOrigem = orgPair.first,
                    contaOrigemNome = orgPair.second,
                    contaDestino = destPair.first ?: "",
                    contaDestinoNome = destPair.second ?: "",
                    valor = valUnit,
                    tipo = tipo,
                    data = date,
                    saldoanterior = readSaldosOrigemAnterior[i],
                    saldofinal = readSaldosOrigemFinal[i]
                )
            )
        }
    }
}
