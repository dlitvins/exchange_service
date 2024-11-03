package com.litvin.exchange.outbound.cache

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CurrencyRateCacheImplTest {
    private val sut = CurrencyRateCacheImpl()

    @BeforeEach
    fun init() {
        sut.replaceCurrencyRates(emptyMap())
    }

    @Test
    fun `store currency rates`() {
        sut.findCurrencyRate("USD") shouldBe null

        sut.addCurrencyRate("USD", BigDecimal("1.1121"))

        val result = sut.findCurrencyRate("USD")
        result shouldBe BigDecimal("1.1121")
    }

    @Test
    fun `when replace currency rates then remove not existing currency rates`() {
        sut.addCurrencyRate("USD", BigDecimal("1.1121"))
        sut.findCurrencyRate("USD") shouldBe BigDecimal("1.1121")

        sut.replaceCurrencyRates(emptyMap())

        sut.findCurrencyRate("USD") shouldBe null
    }

    @Test
    fun `when replace currency rates then replace existing currency rates`() {
        sut.addCurrencyRate("USD", BigDecimal("1.1121"))
        sut.findCurrencyRate("USD") shouldBe BigDecimal("1.1121")

        sut.replaceCurrencyRates(mapOf("USD" to BigDecimal("2.3333")))

        sut.findCurrencyRate("USD") shouldBe BigDecimal("2.3333")
    }
}
