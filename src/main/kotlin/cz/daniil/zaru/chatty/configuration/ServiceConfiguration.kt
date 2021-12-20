package cz.daniil.zaru.chatty.configuration

import cz.daniil.zaru.chatty.property.ApplicationProperty
import cz.daniil.zaru.chatty.service.ConversationService
import cz.daniil.zaru.chatty.service.JokeService
import cz.daniil.zaru.chatty.service.RateConversionService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ServiceConfiguration {

    @Bean
    fun questionService(
        rateConversionService: RateConversionService,
        jokeService: JokeService,
        applicationProperty: ApplicationProperty
    ) = ConversationService(
        rateConversionService,
        jokeService,
        applicationProperty
    )

    @Bean
    fun rateConversionService(webClient: WebClient, applicationProperty: ApplicationProperty) =
        RateConversionService(webClient, applicationProperty)

    @Bean
    fun jokeService(webClient: WebClient, applicationProperty: ApplicationProperty) =
        JokeService(webClient, applicationProperty)

}
