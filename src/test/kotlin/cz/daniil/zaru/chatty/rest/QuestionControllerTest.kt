package cz.daniil.zaru.chatty.rest

import cz.daniil.zaru.chatty.model.QuestionListDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionControllerTest {

    companion object {
        private const val QUESTIONS_URL = "/chatty/questions"
    }

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Should return all available questions`() {
        val response = restTemplate.getForEntity(
            QUESTIONS_URL,
            QuestionListDto::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.questions).isEqualTo(
            listOf(
                "Convert <amount> <from> to <to>",
                "How much is <amount> <from> in <to>",
                "Can you tell me a joke?",
                "Tell me a joke",
                "Hello"
            )
        )
    }

}