package com.litvin.exchange.domain.usecase

import com.litvin.exchange.outbound.fake.CurrencyRateCacheFake
import com.litvin.exchange.outbound.fake.EcbRepositoryFake
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CurrencyRateCacheUpdateUseCaseImplTest {
    private val currencyRateCacheFake = CurrencyRateCacheFake()
    private val ecbRepositoryFake = EcbRepositoryFake()

    private val sut =
        CurrencyRateCacheUpdateUseCaseImpl(
            currencyRateCache = currencyRateCacheFake,
            ecbRepository = ecbRepositoryFake,
        )

    @BeforeEach
    fun init() {
        currencyRateCacheFake.clear()
        ecbRepositoryFake.clear()
    }

    @Test
    fun `when cache reset replace with new value`() {
        currencyRateCacheFake.addCurrencyRate("AUD", BigDecimal("1.11"))
        ecbRepositoryFake.givenRate("AUD", BigDecimal("2.22"))

        sut.resetCache()

        currencyRateCacheFake.findCurrencyRate("AUD") shouldBe BigDecimal("2.22")
    }

    @Test
    fun `when cache reset add new value`() {
        ecbRepositoryFake.givenRate("USD", BigDecimal("3.33"))

        sut.resetCache()

        currencyRateCacheFake.findCurrencyRate("USD") shouldBe BigDecimal("3.33")
    }

    @Test
    fun `when cache reset remove value`() {
        currencyRateCacheFake.addCurrencyRate("AUD", BigDecimal("1.11"))

        sut.resetCache()

        currencyRateCacheFake.findCurrencyRate("AUD") shouldBe null
    }
}
