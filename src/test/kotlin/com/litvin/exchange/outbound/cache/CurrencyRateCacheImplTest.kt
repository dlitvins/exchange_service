package com.litvin.exchange.outbound.cache

import com.litvin.exchange.domain.configuration.CachingConfig
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CurrencyRateCacheImplTest {
    private val config = CachingConfig()
    private val sut = CurrencyRateCacheImpl(config.caffeineCacheManager())

    @BeforeEach
    fun init() {
        sut.clear()
    }

    @Test
    fun `store currency rates`() {
        sut.findCurrencyRate("USD") shouldBe null

        sut.addCurrencyRate("USD", BigDecimal("1.1121"))

        val result = sut.findCurrencyRate("USD")
        result shouldBe BigDecimal("1.1121")
    }

    @Test
    fun `clear currency rates`() {
        sut.addCurrencyRate("USD", BigDecimal("1.1121"))
        sut.findCurrencyRate("USD") shouldBe BigDecimal("1.1121")

        sut.clear()

        sut.findCurrencyRate("USD") shouldBe null
    }
}
