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
    ): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    /**
     * POST /clientes
     * Autocadastro de cliente.
     */
    @PostMapping
    fun autocadastro(
        @RequestBody request: AutocadastroInfo
    ): ResponseEntity<Void> {
        // Lógica de salvar cliente

        // Retorna HTTP 201 (Created) em caso de sucesso
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    /**
     * GET /clientes/{cpf}
     * Consulta os dados de um cliente específico.
     */
    @GetMapping("/{cpf}")
    fun consultarCliente(
        @PathVariable cpf: String
    ): ResponseEntity<DadosClienteResponse> {
        // Lógica de busca por CPF

        return ResponseEntity.ok().build() // Substituir pelo objeto DadosClienteResponse
    }

    /**
     * PUT /clientes/{cpf}
     * Altera os dados de perfil do cliente.
     */
    @PutMapping("/{cpf}")
    fun alterarPerfil(
        @PathVariable cpf: String,
        @RequestBody request: PerfilInfo
    ): ResponseEntity<Void> {
        // Lógica de atualização

        return ResponseEntity.ok().build()
    }

    /**
     * POST /clientes/{cpf}/aprovar
     * Aprova o cliente com o CPF passado e cria a conta.
     */
    @PostMapping("/{cpf}/aprovar")
    fun aprovarCliente(
        @PathVariable cpf: String
    ): ResponseEntity<ContaResponse> {
        // Lógica de aprovação

        return ResponseEntity.ok().build() // Substituir pelo objeto ContaResponse
    }

    /**
     * POST /clientes/{cpf}/rejeitar
     * Rejeita o cliente com o CPF passado informando o motivo.
     */
    @PostMapping("/{cpf}/rejeitar")
    fun rejeitarCliente(
        @PathVariable cpf: String,
        @RequestBody request: RejeicaoRequest
    ): ResponseEntity<Void> {
        // Lógica de rejeição usando o request.motivo

        return ResponseEntity.ok().build()
    }
}
