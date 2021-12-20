package cz.daniil.zaru.chatty.domain

import java.time.Instant

data class Rates(val rates: Map<String, Double>, val updatedAt: Instant = Instant.now())
data class Joke(val value: String)
data class ParsedConversion(val from: String, val to: String, val amount: Double)