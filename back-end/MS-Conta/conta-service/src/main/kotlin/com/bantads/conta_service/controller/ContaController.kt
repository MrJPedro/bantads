package com.bantads.conta_service.controller

import com.bantads.conta_service.dto.*
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
        @RequestBody request: CriarContaDTO
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
        @RequestBody request: DepositoRequestDTO
        ): ResponseEntity<Any> {
        transferenciaService.depositar(numero, request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/sacar") 
    fun sacar(
        @PathVariable numero: String, 
        @RequestBody request: SaqueRequestDTO
        ): ResponseEntity<Any> {
        transferenciaService.sacar(numero, request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/transferir")
    fun transferir(
    @PathVariable numero: String, 
    @RequestBody request: TransferenciaRequestDTO
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
        return ResponseEntity.ok(transferencias)
    }

    @GetMapping("/cliente/{cpf}")
    fun getContaPorCliente(
        @PathVariable cpf: String
    ): ResponseEntity<Any> {
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
        @RequestBody request: AtualizarGerenteDTO
    ): ResponseEntity<ContaDetalhesDTO> {
        val response = contaService.atualizarGerente(numero, request.gerente)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/top3")
    fun getTop3Contas(): ResponseEntity<List<ContaDetalhesDTO>> {
        return ResponseEntity.ok(contaService.obterTop3Contas())
    }
}
