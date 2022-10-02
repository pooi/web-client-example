package com.example.webclient.factory

import com.example.webclient.factory.properties.WebClientProperties
import org.springframework.web.reactive.function.client.WebClient

class WebClientPair(
    val webClient: WebClient,
    val properties: WebClientProperties
)
