package com.bantads.gerente_service

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableRabbit
@SpringBootApplication
class GerenteServiceApplication

fun main(args: Array<String>) {
	runApplication<GerenteServiceApplication>(*args)
}