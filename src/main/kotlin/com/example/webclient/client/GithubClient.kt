package com.example.webclient.client

import com.example.webclient.factory.WebClientPair
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import reactor.util.retry.Retry
import java.util.*

@Component
@DependsOn("webClientFactory")
class GithubClient(
    @Qualifier("githubWebClientPair") webClientPair: WebClientPair
) {
    private val webClient = webClientPair.webClient
        .mutate()
        .filter { request, next ->
            next.exchange(
                ClientRequest.from(request)
                    .header("LOGGING-ID", UUID.randomUUID().toString())
                    .build()
            )
        }
        .build()
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
