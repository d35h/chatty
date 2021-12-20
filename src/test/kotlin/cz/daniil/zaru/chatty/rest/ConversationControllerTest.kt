package cz.daniil.zaru.chatty.rest

import cz.daniil.zaru.chatty.model.AnswerDto
import cz.daniil.zaru.chatty.model.ConversationDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ConversationControllerTest {

    companion object {
        private const val CONVERSATION_URL = "/chatty/conversation"
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Should reply with joke`() {
        val response = restTemplate.postForEntity(
            CONVERSATION_URL,
            ConversationDto().apply { message = "Tell me a joke" },
            AnswerDto::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        // Since it's an integration test and we don't know which joke will arrive over the wire,
        // we check only if the response is not empty, i.e. something was sent
        assertThat(response.body!!.answer).isNotEmpty
    }

    @Test
    fun `Should reply with a greetings message`() {
        val response = restTemplate.postForEntity(
            CONVERSATION_URL,
            ConversationDto().apply { message = "Hello" },
            AnswerDto::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.answer).isEqualTo("Hi, I'm Chatty. How can I help you?")
    }

    @Test
    fun `Should error when input unrecognized`() {
        val response = restTemplate.postForEntity(
            CONVERSATION_URL,
            ConversationDto().apply { message = "MeNoRecognizy" },
            AnswerDto::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body!!.answer).isEqualTo("I'm sorry I don't understand the question")
    }

    @Test
    fun `Should reply with rates conversion`() {
        val expectedConversionResponse = "It's \\d+.?\\d+ AUD".toRegex()
        val response = restTemplate.postForEntity(
            CONVERSATION_URL,
            ConversationDto().apply { message = "Convert 10 USD to AUD" },
            AnswerDto::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        // Since currency rates are volatile, we just make sure that some conversion is sent back
        assertThat(expectedConversionResponse.matches(response.body!!.answer)).isTrue
    }

}