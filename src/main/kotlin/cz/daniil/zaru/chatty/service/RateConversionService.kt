package cz.daniil.zaru.chatty.service

import cz.daniil.zaru.chatty.domain.Rates
import cz.daniil.zaru.chatty.property.ApplicationProperty
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

class RateConversionService(
    private val webClient: WebClient,
    private val applicationProperty: ApplicationProperty
) {
    companion object {
        private var CACHED_RATES: Rates? = null
    }

    suspend fun convert(from: String, to: String, amount: Double): Double {
        // Defensive programming
        val fromUpperCase = from.uppercase()
        val toUpperCase = to.uppercase()

        updateRates()
        val fromRate = if (fromUpperCase == applicationProperty.baseCurrency) 1.0 else 1.0.div(CACHED_RATES!!.rates[fromUpperCase]!!)
        val toRate = if (toUpperCase == applicationProperty.baseCurrency) 1.0 else CACHED_RATES!!.rates[toUpperCase]!!
        return amount * fromRate * toRate
    }

    fun invalidateCache() {
        CACHED_RATES = null
    }

    private suspend fun updateRates() {
        if (CACHED_RATES == null || ChronoUnit.DAYS.between(Instant.now(), CACHED_RATES!!.updatedAt) >= 1) {
            CACHED_RATES = getAllRatesFromBaseCurrency()
        }
    }

    private suspend fun getAllRatesFromBaseCurrency(): Rates =
        webClient
            .get()
            .uri("${applicationProperty.baseExchangeUrl}/latest?from=${applicationProperty.baseCurrency}")
            .retrieve()
            .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals) {
                    Mono.error(
                        ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            MessageService.getUnreachableServiceMessage(applicationProperty.baseExchangeUrl))
                    )
            }
            .bodyToMono(Rates::class.java)
            .awaitSingle()

}

