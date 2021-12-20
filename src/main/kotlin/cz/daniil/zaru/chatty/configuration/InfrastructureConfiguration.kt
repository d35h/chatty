package cz.daniil.zaru.chatty.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class InfrastructureConfiguration {

    @Bean
    fun httpClient() = WebClient.create()

}
