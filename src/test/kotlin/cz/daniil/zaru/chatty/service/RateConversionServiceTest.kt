package cz.daniil.zaru.chatty.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@SpringBootTest(
    properties = ["application.base-exchange-url=http://localhost:9090/test/"]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RateConversionServiceTest {

    private var wireMockServer = WireMockServer(9090)

    @Autowired
    private lateinit var rateConversionService: RateConversionService

    @BeforeAll
    fun setUp() {
        wireMockServer.start()
    }

    @AfterAll
    fun tearDown() {
        wireMockServer.stop()
    }

    @BeforeEach
    fun setUpIndividually() {
        rateConversionService.invalidateCache()
    }

    @Test
    fun `Should convert correctly`() = runBlocking<Unit> {
        wireMockServer.stubFor(
            WireMock.get("/test/latest?from=EUR").willReturn(
                WireMock.okJson("""{"rates": {"USD": 1.2, "AUD": 1.5}}""")
            )
        )
        assertThat(rateConversionService.convert("USD", "AUD", 2.0)).isEqualTo(2.5)
    }

    @Test
    fun `Should return bad request when API is down`() = runBlocking<Unit> {
        wireMockServer.stubFor(
            WireMock.get("/test/latest?from=EUR").willReturn(
                serverError()
            )
        )
        val actual = assertThrows<ResponseStatusException> {
            rateConversionService.convert("USD", "AUD", 2.0)
        }

        assertThat(actual.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(actual.reason).isEqualTo("I'm sorry, I can't answer now, service on url " +
                "http://localhost:9090/test/ seems to be unreachable")
    }

}
