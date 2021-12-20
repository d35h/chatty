package cz.daniil.zaru.chatty.service

import cz.daniil.zaru.chatty.domain.Joke
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@SpringBootTest
class ConversationServiceTest {

    @Autowired
    private lateinit var service: ConversationService

    @Test
    fun `Should answer with joke`() = runBlocking<Unit> {
        var actual = service.getAnswer("Tell me a joke")
        val expected = "Joking again"

        assertThat(actual).isEqualTo(expected)

        actual = service.getAnswer("Can you tell me a joke?")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Should answer with rate conversion`() = runBlocking<Unit> {
        var actual = service.getAnswer("Convert 123.33 USD to EUR")
        val expected = "It's 100.00 EUR"

        assertThat(actual).isEqualTo(expected)

        actual = service.getAnswer("How much is 123 USD in EUR")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Should answer with greetings`() = runBlocking<Unit> {
        val actual = service.getAnswer("Hello")
        val expected = "Hi, I'm Chatty. How can I help you?"

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Should answer with unsupported question message`() = runBlocking<Unit> {
        val actual = service.getAnswer("Zdravim")
        val expected = "I'm sorry I don't understand the question"

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Should be case insensitive`() = runBlocking<Unit> {
        val actual = service.getAnswer("hElLo")
        val expected = "Hi, I'm Chatty. How can I help you?"

        assertThat(actual).isEqualTo(expected)
    }

    @TestConfiguration
    class MockedConfig {
        @Bean
        @Primary
        fun mockedJokeService() = mockk<JokeService>().apply {
            coEvery { getRandomJoke() } returns Joke("Joking again")
        }

        @Bean
        @Primary
        fun mockedRateConversionService() = mockk<RateConversionService>(relaxed = true).apply {
            coEvery { convert("usd", "eur", 123.33) } returns 100.0
            coEvery { convert("usd", "eur", 123.0) } returns 100.0
        }
    }

}
