package com.bantads.conta_service.service

import com.bantads.conta_service.dto.CriarContaDTO
import com.bantads.conta_service.dto.ContaDetalhesDTO
import com.bantads.conta_service.entity.comando.Conta
import com.bantads.conta_service.repository.comando.ContaRepositoryWrite
import com.bantads.conta_service.repository.comando.TransferenciaRepositoryWrite
import com.bantads.conta_service.repository.leitura.ContaRepositoryRead
import com.bantads.conta_service.repository.leitura.TransferenciaRepositoryRead
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import kotlin.random.Random

@Service
@Transactional
class ContaService(
    private val contaRepositoryRead: ContaRepositoryRead,
    private val transferenciaRepositoryRead: TransferenciaRepositoryRead,
    private val contaRepositoryWrite: ContaRepositoryWrite,
    private val transferenciaRepositoryWrite: TransferenciaRepositoryWrite
) {

    fun criar(numero: String, request: CriarContaDTO): Any {
        val conta = contaRepositoryWrite.save(
            Conta(
                cliente = request.cliente,
                numero = numero,
                saldo = request.saldo.setScale(2, RoundingMode.HALF_EVEN),
                limite = request.limite.setScale(2, RoundingMode.HALF_EVEN),
                gerente = request.gerente,
                criacao = request.criacao
            )
        )

        return conta
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
        return if (metadeSalario > BigDecimal(2000).setScale(2, RoundingMode.HALF_EVEN)) {
            BigDecimal(2000).setScale(2, RoundingMode.HALF_EVEN)
        } else {
            metadeSalario
        }
    }

    fun obterContaPorCliente(cpf: String): ContaDetalhesDTO? {
        val conta = contaRepositoryRead.findFirstByCliente(cpf) ?: return null
        return ContaDetalhesDTO(
            cliente = conta.cliente,
            numero = conta.numero,
            saldo = conta.saldo.setScale(2, RoundingMode.HALF_EVEN),
            limite = conta.limite.setScale(2, RoundingMode.HALF_EVEN),
            gerente = conta.gerente,
            criacao = conta.criacao.toString()
        )
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
}