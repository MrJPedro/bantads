package com.bantads.conta_service.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/contas")
class ContaController {

    @GetMapping("/{numero}/saldo")
    fun getSaldo(
        @PathVariable numero: String
        ): ResponseEntity<SaldoResponseDTO> {
        // TODO: Implementar consulta de saldo
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/depositar")
    fun depositar(
        @PathVariable numero: String, 
        @RequestBody request: DepositoRequestDTO
        ): ResponseEntity<SaldoResponseDTO> {
        // TODO: Implementar lógica de depósito (R5)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/sacar")
    fun sacar(
        @PathVariable numero: String, 
        @RequestBody request: SaqueRequestDTO
        ): ResponseEntity<SaldoResponseDTO> {
        // TODO: Implementar lógica de saque (R6)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/transferir")
    fun transferir(
    @PathVariable numero: String, 
    @RequestBody request: TransferenciaRequestDTO
    ): ResponseEntity<SaldoResponseDTO> {
        // TODO: Implementar lógica de transferência (R7)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{numero}/extrato")
    fun getExtrato(
        @PathVariable numero: String,
        @RequestParam(required = false) dataInicio: String?,
        @RequestParam(required = false) dataFim: String?
    ): ResponseEntity<ExtratoResponseDTO> {
        // TODO: Implementar consulta de extrato por período (R8)
        return ResponseEntity.ok().build()
    }
}