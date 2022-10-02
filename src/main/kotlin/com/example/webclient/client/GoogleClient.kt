package com.example.webclient.client

import com.example.webclient.factory.WebClientPair
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import reactor.util.retry.Retry

@Component
@DependsOn("webClientFactory")
class GoogleClient(
    @Qualifier("googleWebClientPair") webClientPair: WebClientPair
) {
    private val webClient = webClientPair.webClient
    private val retrySpec = Retry.backoff(
        webClientPair.properties.maxRetry, webClientPair.properties.retryDelay
    )

    suspend fun getMainPage(): Map<*, *>? {
        return webClient
            .get()
            .uri("main")
            .retrieve()
            .bodyToMono(Map::class.java)
            .retryWhen(retrySpec)
            .awaitSingleOrNull()
    }
}
