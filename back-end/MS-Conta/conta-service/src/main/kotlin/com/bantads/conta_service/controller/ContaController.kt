package com.bantads.conta_service.controller

import com.bantads.conta_service.dtos.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/contas")
class ContaController {

    @GetMapping("/{numero}/saldo")
    fun getSaldo(
        @PathVariable numero: String
        ): ResponseEntity<Any> {
        // Implementar a lógica d consulta do saldo
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/depositar")
    fun depositar(
        @PathVariable numero: String, 
        @RequestBody request: DepositoRequestDTO
        ): ResponseEntity<Any> {
        // Implementar a lógica do depósiot
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/sacar") 
    fun sacar(
        @PathVariable numero: String, 
        @RequestBody request: SaqueRequestDTO
        ): ResponseEntity<Any> {
        // Implementar a lógica do saque
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{numero}/transferir")
    fun transferir(
    @PathVariable numero: String, 
    @RequestBody request: TransferenciaRequestDTO
    ): ResponseEntity<Any> {
        // Implementar a lógica da transferência
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{numero}/extrato")
    fun getExtrato(
        @PathVariable numero: String,
        @RequestParam(required = false) dataInicio: String?,
        @RequestParam(required = false) dataFim: String?
    ): ResponseEntity<Any> {
        // Implementar a lógica de consulta de extrato
        return ResponseEntity.ok().build()
    }
}
