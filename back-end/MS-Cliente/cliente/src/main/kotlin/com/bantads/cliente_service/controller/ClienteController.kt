package com.bantads.cliente_service.controller

import com.bantads.cliente_service.dto.*
import com.bantads.cliente_service.service.ClienteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
class ClienteController (

    private val clienteService: ClienteService

) {
    /**
     * GET /clientes
     * Consulta todos os clientes com base no filtro.
     */
    @GetMapping
    fun listarClientes(
        @RequestParam(name = "filtro", required = false) filtro: String?
    ): ResponseEntity<List<Any>> {
        val clientes = clienteService.listarClientes(filtro)
        return ResponseEntity.ok(clientes)
    }

    /**
     * POST /clientes
     * Autocadastro de cliente.
     */
    @PostMapping
    fun autocadastro(
        @RequestBody request: AutocadastroInfo
    ): ResponseEntity<DadosClienteResponse> {
        val response = clienteService.autocadastro(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * GET /clientes/{cpf}
     * Consulta os dados de um cliente específico.
     */
    @GetMapping("/{cpf}")
    fun consultarCliente(
        @PathVariable cpf: String
    ): ResponseEntity<DadosClienteResponse> {
        val response = clienteService.buscarPorCpf(cpf)
        return ResponseEntity.ok(response)
    }

    /**
     * PUT /clientes/{cpf}
     * Altera os dados de perfil do cliente.
     */
    @PutMapping("/{cpf}")
    fun alterarPerfil(
        @PathVariable cpf: String,
        @RequestBody request: PerfilInfo
    ): ResponseEntity<DadosClienteResponse> {
        val response = clienteService.alterar(cpf, request)
        return ResponseEntity.ok(response)
    }

    /**
     * POST /clientes/{cpf}/aprovar
     * Aprova o cliente com o CPF passado e cria a conta.
     */
    @PostMapping("/{cpf}/aprovar")
    fun aprovarCliente(
        @PathVariable cpf: String
    ): ResponseEntity<DadosClienteResponse> {
        val response = clienteService.aprovar(cpf)
        return ResponseEntity.ok(response) 
    }

    /**
     * POST /clientes/{cpf}/rejeitar
     * Rejeita o cliente com o CPF passado informando o motivo.
     */
    @PostMapping("/{cpf}/rejeitar")
    fun rejeitarCliente(
        @PathVariable cpf: String,
        @RequestBody request: RejeicaoRequest
    ): ResponseEntity<DadosClienteResponse> {
        val response = clienteService.rejeitar(cpf, request)
        return ResponseEntity.ok(response)
    }
}
