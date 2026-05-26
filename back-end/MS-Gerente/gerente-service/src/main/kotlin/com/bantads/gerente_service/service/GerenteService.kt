package com.bantads.gerente_service.service

import com.bantads.gerente_service.config.GERENTE_EVENT_EXCHANGE
import com.bantads.gerente_service.dto.*
import com.bantads.gerente_service.entity.GerenteEntity
import com.bantads.gerente_service.repository.GerenteRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class GerenteService(
    private val gerenteRepository: GerenteRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val restTemplate: RestTemplate
) {

    fun listarTodos(cpf: String? = null): List<DadoGerente> {
        if (!cpf.isNullOrBlank()) {
            val gerente = gerenteRepository.findByCpf(cpf) ?: return emptyList()
            return listOf(toDTO(gerente))
        }

        return gerenteRepository.findAll()
            .sortedBy { it.nome }
            .map { toDTO(it) }
    }

    fun listarDashboard(): List<DashboardGerenteItemDTO> {
        val gerentes = gerenteRepository.findAll()
            .sortedBy { it.nome }

        val relatorioClientes = buscarRelatorioClientes()
        val contasPorGerente = relatorioClientes
            .mapNotNull { toContaDashboard(it) }
            .groupBy { it.gerente }

        return gerentes.map { gerente ->
            val contas = contasPorGerente[gerente.cpf].orEmpty()
            DashboardGerenteItemDTO(
                gerente = toDTO(gerente),
                clientes = contas,
                saldo_positivo = somarSaldoPositivo(contas),
                saldo_negativo = somarSaldoNegativo(contas)
            )
        }
    }

    fun buscarPorCpf(cpf: String): DadoGerente {
        val gerente = gerenteRepository.findByCpf(cpf) 
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")
        
        return toDTO(gerente)
    }

    @Transactional
    fun inserir(dto: DadoGerenteInsercao): DadoGerente {
        if (gerenteRepository.findByCpf(dto.cpf) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado")
        }

        var novoGerente = GerenteEntity(
            cpf = dto.cpf,
            nome = dto.nome,
            email = dto.email,
            telefone = dto.telefone,
            quantidadeClientes = 0
        )
        
        novoGerente = gerenteRepository.save(novoGerente)

        val eventoCriacao = GerenteEvent(
            tipo = "insercao",
            cpfGerente = novoGerente.cpf,
            nome = novoGerente.nome,
            email = novoGerente.email,
            senha = dto.senha
        )
        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.insercao", eventoCriacao)

        val outrosGerentes = gerenteRepository.findAll().filter { it.cpf != novoGerente.cpf }
        
        if (outrosGerentes.isEmpty() || (outrosGerentes.size == 1 && outrosGerentes.first().quantidadeClientes <= 1)) {
            return toDTO(novoGerente)
        }

        val maxContas = outrosGerentes.maxOf { it.quantidadeClientes }
        if (maxContas == 0) {
            return toDTO(novoGerente)
        }

        val gerentesMax = outrosGerentes.filter { it.quantidadeClientes == maxContas }
        val contasPossiveis = mutableListOf<ContaDTO>()

        for (g in gerentesMax) {
            try {
                val url = "http://ms-conta:8083/contas/gerente/${g.cpf}"
                val response = restTemplate.getForObject(url, Array<ContaDTO>::class.java)
                if (response != null) {
                    contasPossiveis.addAll(response)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        if (contasPossiveis.isNotEmpty()) {
            val contaEscolhida = contasPossiveis.filter { it.saldo > BigDecimal.ZERO }.minByOrNull { it.saldo }
                ?: contasPossiveis.minByOrNull { it.saldo }

            if (contaEscolhida != null) {
                val gerenteDoador = gerentesMax.find { it.cpf == contaEscolhida.gerente }
                if (gerenteDoador != null) {
                    if (atualizarContaGerente(contaEscolhida.numero, novoGerente.cpf)) {
                        atualizarClienteGerente(contaEscolhida.cliente, novoGerente.cpf)
                        gerenteDoador.quantidadeClientes -= 1
                        novoGerente.quantidadeClientes += 1
                        gerenteRepository.save(gerenteDoador)
                        gerenteRepository.save(novoGerente)

                        val eventoTransferencia = GerenteEvent(
                            tipo = "transferencia_conta",
                            cpfGerente = novoGerente.cpf,
                            cpfGerenteAnterior = gerenteDoador.cpf,
                            numeroConta = contaEscolhida.numero
                        )
                        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.transferencia", eventoTransferencia)
                    }
                }
            }
        }

        return toDTO(novoGerente)
    }

    @Transactional
    fun alterar(cpf: String, dto: DadoGerenteAtualizacao): DadoGerente {
        val gerente = gerenteRepository.findByCpf(cpf)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")

        gerente.nome = dto.nome
        gerente.email = dto.email
        
        gerenteRepository.save(gerente)

        return toDTO(gerente)
    }

    @Transactional
    fun remover(cpf: String): DadoGerente {
        val gerente = gerenteRepository.findByCpf(cpf)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")

        if (gerenteRepository.count() <= 1) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível remover o último gerente do sistema.")
        }

        val gerenteRemovido = toDTO(gerente)

        val todos = gerenteRepository.findAll().filter { it.cpf != cpf }
        val herdeiro = todos.minByOrNull { it.quantidadeClientes }
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nenhum herdeiro encontrado.")

        val contasGerente = try {
            restTemplate.getForObject("http://ms-conta:8083/contas/gerente/$cpf", Array<ContaDTO>::class.java)
                ?.toList()
                .orEmpty()
        } catch (e: Exception) {
            emptyList()
        }

        val contasAtualizadas = contasGerente.count { conta ->
            val sucessoConta = atualizarContaGerente(conta.numero, herdeiro.cpf)
            if (sucessoConta) {
                atualizarClienteGerente(conta.cliente, herdeiro.cpf)
            }
            sucessoConta
        }

        herdeiro.quantidadeClientes += contasAtualizadas
        gerenteRepository.save(herdeiro)

        gerenteRepository.delete(gerente)

        val evento = GerenteEvent(
            tipo = "remocao",
            cpfGerente = cpf,
            cpfNovoGerente = herdeiro.cpf
        )
        
        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.remocao", evento)

        return gerenteRemovido
    }

    private fun buscarRelatorioClientes(): List<RelatorioClienteCompostoDTO> {
        return try {
            restTemplate.getForObject(
                "http://ms-cliente:8083/clientes?filtro=adm_relatorio_clientes",
                Array<RelatorioClienteCompostoDTO>::class.java
            )?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun toContaDashboard(relatorio: RelatorioClienteCompostoDTO): ContaDashboardDTO? {
        val numeroConta = relatorio.conta ?: return null
        val cpfGerente = relatorio.gerenteCpf ?: return null

        return ContaDashboardDTO(
            cliente = relatorio.cpf,
            numero = numeroConta,
            saldo = relatorio.saldo ?: BigDecimal.ZERO,
            limite = relatorio.limite ?: BigDecimal.ZERO,
            gerente = cpfGerente
        )
    }

    private fun somarSaldoPositivo(contas: List<ContaDashboardDTO>): BigDecimal {
        return contas
            .filter { it.saldo >= BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { total, conta -> total + conta.saldo }
    }

    private fun somarSaldoNegativo(contas: List<ContaDashboardDTO>): BigDecimal {
        return contas
            .filter { it.saldo < BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { total, conta -> total + conta.saldo }
    }

    private fun toDTO(entity: GerenteEntity): DadoGerente {
        return DadoGerente(
            id = entity.id,
            cpf = entity.cpf,
            nome = entity.nome,
            email = entity.email,
            telefone = entity.telefone,
            quantidadeClientes = entity.quantidadeClientes
        )
    }
}
