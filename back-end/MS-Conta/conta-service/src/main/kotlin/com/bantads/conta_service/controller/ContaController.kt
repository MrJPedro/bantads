package com.bantads.conta_service.controller

import com.bantads.conta_service.dto.*
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.bantads.conta_service.service.TransferenciaService
import com.bantads.conta_service.service.ContaService

@RestController
@RequestMapping("/contas")
class ContaController(
    private val transferenciaService: TransferenciaService,
    private val contaService: ContaService
) {

    @PostMapping("/{numero}")
    fun criarConta(
        @PathVariable numero: String,
        @Valid @RequestBody request: ContaDTO
    ): ResponseEntity<Any>{
        contaService.criar(numero, request);
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{numero}/saldo")
    fun getSaldo(
        @PathVariable numero: String
        ): ResponseEntity<Any> {
        val saldo = transferenciaService.obterSaldo(numero)
        return ResponseEntity.ok(SaldoResponseDTO(numero, saldo))
    }

    @PostMapping("/{numero}/depositar")
    fun depositar(
        @PathVariable numero: String, 
        @Valid @RequestBody request: DepositoRequestDTO
        ): ResponseEntity<Any> {
        transferenciaService.depositar(numero, request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/sacar") 
    fun sacar(
        @PathVariable numero: String, 
        @Valid @RequestBody request: SaqueRequestDTO
        ): ResponseEntity<Any> {
        transferenciaService.sacar(numero, request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/transferir")
    fun transferir(
    @PathVariable numero: String, 
    @Valid @RequestBody request: TransferenciaRequestDTO
    ): ResponseEntity<Any> {
        transferenciaService.transferir(numero, request)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{numero}/extrato")
    fun getExtrato(
        @PathVariable numero: String,
        @RequestParam(required = false) dataInicio: String?,
        @RequestParam(required = false) dataFim: String?
    ): ResponseEntity<Any> {
        val transferencias = transferenciaService.obterExtrato(numero, dataInicio, dataFim)
        val saldo = transferenciaService.obterSaldo(numero)
        val items = transferencias.map {
            val tipoMapeado = when (it.tipo.uppercase()) {
                "DEPOSITO" -> "depósito"
                "SAQUE" -> "saque"
                "TRANSFERENCIA" -> "transferência"
                else -> it.tipo.lowercase()
            }
            ExtratoItemDTO(
                data = it.data.toString(),
                tipo = tipoMapeado,
                origem = if (it.contaOrigem.isNullOrBlank()) null else it.contaOrigem,
                destino = if (it.contaDestino.isNullOrBlank()) null else it.contaDestino,
                valor = it.valor
            )
        }
        return ResponseEntity.ok(ExtratoResponse(numero, saldo, items))
    }

    @GetMapping("/reboot")
    fun reboot(): ResponseEntity<Void> {
        contaService.reboot()
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/cliente/{cpf}")
    fun getContaPorCliente(
        @PathVariable cpf: String
    ): ResponseEntity<ContaDetalhesDTO> {
        val conta = contaService.obterContaPorCliente(cpf) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(conta)
    }

    @GetMapping("/gerente/{cpf}")
    fun getContasPorGerente(
        @PathVariable cpf: String
    ): ResponseEntity<List<ContaDetalhesDTO>> {
        return ResponseEntity.ok(contaService.obterContasPorGerente(cpf))
    }

    @PutMapping("/{numero}/gerente")
    fun atualizarGerente(
        @PathVariable numero: String,
        @Valid @RequestBody request: AtualizarGerenteDTO
    ): ResponseEntity<ContaDetalhesDTO> {
        val response = contaService.atualizarGerente(numero, request.gerente)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{numero}")
    fun getContaPorNumero(
        @PathVariable numero: String
    ): ResponseEntity<ContaDetalhesDTO> {
        val conta = contaService.obterContaPorNumero(numero) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(conta)
    }

    @GetMapping("/top3")
    fun getTop3Contas(): ResponseEntity<List<ContaDetalhesDTO>> {
        return ResponseEntity.ok(contaService.obterTop3Contas())
    }
}
