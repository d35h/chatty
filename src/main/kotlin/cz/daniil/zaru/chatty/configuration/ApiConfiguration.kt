package cz.daniil.zaru.chatty.configuration

import cz.daniil.zaru.chatty.property.ApplicationProperty
import cz.daniil.zaru.chatty.rest.ConversationController
import cz.daniil.zaru.chatty.rest.QuestionController
import cz.daniil.zaru.chatty.service.ConversationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiConfiguration {
    @Bean
    fun questionController(applicationProperty: ApplicationProperty) = QuestionController(applicationProperty)

    @Bean
    fun conversationController(conversationService: ConversationService) =
        ConversationController(conversationService)
}