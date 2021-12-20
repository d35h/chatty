package cz.daniil.zaru.chatty.rest

import cz.daniil.zaru.chatty.api.QuestionsApiDelegate
import cz.daniil.zaru.chatty.model.QuestionListDto
import cz.daniil.zaru.chatty.property.ApplicationProperty
import kotlinx.coroutines.reactor.mono
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class QuestionController(private val applicationProperty: ApplicationProperty) : QuestionsApiDelegate {
    override fun getAll(exchange: ServerWebExchange): Mono<ResponseEntity<QuestionListDto>> = mono {
        ResponseEntity.ok(
            QuestionListDto().apply {
                questions = applicationProperty.conversation.questions.values.flatten()
            }
        )
    }
}