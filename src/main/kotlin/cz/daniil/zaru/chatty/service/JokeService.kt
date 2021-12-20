package cz.daniil.zaru.chatty.service

import cz.daniil.zaru.chatty.domain.Joke
import cz.daniil.zaru.chatty.property.ApplicationProperty
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

open class JokeService(
    private val webClient: WebClient,
    private val applicationProperty: ApplicationProperty
) {

    companion object {
        val FALLBACK_JOKES = listOf(
            Joke("I'm afraid for the calendar. Its days are numbered."),
            Joke("Singing in the shower is fun until you get soap in your mouth. Then it's a soap opera."),
            Joke("Dear Math, grow up and solve your own problems."),
        )
    }

    suspend fun getRandomJoke(): Joke = webClient
        .get()
        .uri("${applicationProperty.baseJokesUrl}/jokes/random")
        .retrieve()
        .bodyToMono(Joke::class.java)
        .onErrorResume(
            WebClientResponseException::class.java
        ) { ex ->
            if (ex.statusCode.isError) Mono.just(FALLBACK_JOKES.shuffled().first()) else Mono.error(ex)
        }.awaitSingle()

}