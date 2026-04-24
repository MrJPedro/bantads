package com.bantads.gerente_service.controller

import com.bantads.gerente_service.dto.*
import com.bantads.gerente_service.service.GerenteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/gerentes")
class GerenteController(
    private val gerenteService: GerenteService
) {

    /*GET /gerentes*/
    @GetMapping
    fun listarGerentes(): ResponseEntity<List<DadoGerente>> {
        val gerentes = gerenteService.listarTodos()
        return ResponseEntity.ok(gerentes)
    }

    /*GET /gerentes/{cpf}*/
    @GetMapping("/{cpf}")
    fun buscarGerente(@PathVariable cpf: String): ResponseEntity<DadoGerente> {
        val gerente = gerenteService.buscarPorCpf(cpf)
        return ResponseEntity.ok(gerente)
    }

    /*POST /gerentes*/
    @PostMapping
    fun inserirGerente(@RequestBody request: DadoGerenteInsercao): ResponseEntity<DadoGerente> {
        val novoGerente = gerenteService.inserir(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(novoGerente)
    }

    /*PUT /gerentes/{cpf}*/
    @PutMapping("/{cpf}")
    fun alterarGerente(
        @PathVariable cpf: String,
        @RequestBody request: DadoGerenteAtualizacao
    ): ResponseEntity<DadoGerente> {
        val gerenteAtualizado = gerenteService.alterar(cpf, request)
        return ResponseEntity.ok(gerenteAtualizado)
    }

    /*DELETE /gerentes/{cpf}*/
    @DeleteMapping("/{cpf}")
    fun removerGerente(@PathVariable cpf: String): ResponseEntity<Void> {
        gerenteService.remover(cpf)
        return ResponseEntity.status(HttpStatus.ACCEPTED).build() 
    }
}