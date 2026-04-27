package com.bantads.conta_service.controller

import com.bantads.conta_service.dtos.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/contas")
class ContaController {

    @GetMapping("/{cpf}/saldo")
    fun getSaldo(
        @PathVariable cpf: String
        ): ResponseEntity<Any> {
        // Implementar a lógica d consulta do saldo
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{cpf}/depositar")
    fun depositar(
        @PathVariable cpf: String, 
        @RequestBody request: DepositoRequestDTO
        ): ResponseEntity<Any> {
        // Implementar a lógica do depósiot
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{cpf}/sacar") 
    fun sacar(
        @PathVariable cpf: String, 
        @RequestBody request: SaqueRequestDTO
        ): ResponseEntity<Any> {
        // Implementar a lógica do saque
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{cpf}/transferir")
    fun transferir(
    @PathVariable cpf: String, 
    @RequestBody request: TransferenciaRequestDTO
    ): ResponseEntity<Any> {
        // Implementar a lógica da transferência
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{cpf}/extrato")
    fun getExtrato(
        @PathVariable cpf: String,
        @RequestParam(required = false) dataInicio: String?,
        @RequestParam(required = false) dataFim: String?
    ): ResponseEntity<Any> {
        // Implementar a lógica de consulta de extrato
        return ResponseEntity.ok().build()
    }
}