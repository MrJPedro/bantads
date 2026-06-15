package com.bantads.gerente_service.controller

import com.bantads.gerente_service.dto.*
import com.bantads.gerente_service.service.GerenteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/gerentes")
class GerenteController(
    private val gerenteService: GerenteService
) {

    /*GET /gerentes R19 - LISTAGEM DE GERENTES*/ 
    @GetMapping
    fun listarGerentes(
        @RequestParam(name = "cpf", required = false) cpf: String?
    ): ResponseEntity<List<DadoGerente>> {
        val gerentes = gerenteService.listarTodos(cpf)
        return ResponseEntity.ok(gerentes)
    }

    /*GET /gerentes/{cpf} R19 - LISTAGEM DE GERENTES POR CPF*/
    @GetMapping("/{cpf}")
    fun buscarGerente(@PathVariable cpf: String): ResponseEntity<DadoGerente> {
        val gerente = gerenteService.buscarPorCpf(cpf)
        return ResponseEntity.ok(gerente)
    }

    /*POST /gerentes R17 - INSERCAO DE GERENTE*/
    @PostMapping
    fun inserirGerente(@RequestBody request: DadoGerenteInsercao): ResponseEntity<DadoGerente> {
        val novoGerente = gerenteService.inserir(request)
        return ResponseEntity.ok(novoGerente)
    }

    /*PUT /gerentes/{cpf} R20 - ALTERACAO DE GERENTE*/
    @PutMapping("/{cpf}")
    fun alterarGerente(
        @PathVariable cpf: String,
        @RequestBody request: DadoGerenteAtualizacao
    ): ResponseEntity<DadoGerente> {
        val gerenteAtualizado = gerenteService.alterar(cpf, request)
        return ResponseEntity.ok(gerenteAtualizado)
    }

    /*DELETE /gerentes/{cpf} R18 - REMOCAO DE GERENTE*/
    @DeleteMapping("/{cpf}")
    fun removerGerente(@PathVariable cpf: String): ResponseEntity<DadoGerente> {
        val gerenteRemovido = gerenteService.remover(cpf)
        return ResponseEntity.ok(gerenteRemovido)
    }
}
