package com.example.webclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebClientExampleApplication

fun main(args: Array<String>) {
    runApplication<WebClientExampleApplication>(*args)
}
