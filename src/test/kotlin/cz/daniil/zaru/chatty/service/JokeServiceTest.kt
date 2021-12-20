package cz.daniil.zaru.chatty.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import cz.daniil.zaru.chatty.service.JokeService.Companion.FALLBACK_JOKES
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = ["application.base-jokes-url=http://localhost:9090/test/"]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JokeServiceTest {

    private var wireMockServer = WireMockServer(9090)

    @Autowired
    private lateinit var jokeService: JokeService

    @BeforeAll
    fun setUp() {
        wireMockServer.start()
    }

    @AfterAll
    fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    fun `Should return joke from API when they are up and running`() = runBlocking<Unit> {
        wireMockServer.stubFor(
            get("/test/jokes/random").willReturn(
                okJson("""{"value": "joke1"}""")
            )
        )
        assertThat(jokeService.getRandomJoke().value).isEqualTo("joke1")
    }

    @Test
    fun `Should return joke from cache when API when they are down`() = runBlocking<Unit> {
        wireMockServer.stubFor(
            get("/test/jokes/random").willReturn(
                serverError()
            )
        )
        assertThat(FALLBACK_JOKES.contains(jokeService.getRandomJoke())).isTrue()
    }

}