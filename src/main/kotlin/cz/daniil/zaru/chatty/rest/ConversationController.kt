package cz.daniil.zaru.chatty.rest

import cz.daniil.zaru.chatty.api.ConversationApiDelegate
import cz.daniil.zaru.chatty.model.AnswerDto
import cz.daniil.zaru.chatty.model.ConversationDto
import cz.daniil.zaru.chatty.service.ConversationService
import cz.daniil.zaru.chatty.service.MessageService.UNSUPPORTED_QUESTION
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class ConversationController(private val conversationService: ConversationService) : ConversationApiDelegate {

    override fun answerQuestion(
        conversationDto: Mono<ConversationDto>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<AnswerDto>> = mono {
        val answerMessage = conversationService.getAnswer(conversationDto.awaitSingle().message.lowercase())
        ResponseEntity(
            AnswerDto().apply { answer = answerMessage },
            if (answerMessage == UNSUPPORTED_QUESTION) HttpStatus.BAD_REQUEST else HttpStatus.OK
        )
    }
}