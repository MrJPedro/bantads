package com.bantads.conta_service

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableRabbit
@SpringBootApplication
class ContaServiceApplication

fun main(args: Array<String>) {
	runApplication<ContaServiceApplication>(*args)
}
