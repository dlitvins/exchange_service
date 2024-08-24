package com.litvin.exchange.domain.usecase.exchangeCalculator

import com.litvin.exchange.domain.model.Fee
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@Suppress("ClassName")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExchangeCalculatorUseCaseImplTest_PersistedFee : ExchangeCalculatorUseCaseImplTestKit(BigDecimal.ZERO) {
    @Test
    fun `exchange without fee`() {
        currencyRateCache.addCurrencyRate("USD", BigDecimal("4"))
        currencyRateCache.addCurrencyRate("AUD", BigDecimal("2"))
        feeRepositoryFake.createOrUpdateFee(Fee(1L, "USD", "AUD", BigDecimal("0.5")))

        val result = sut.calculateConversion(BigDecimal("200"), "USD", "AUD")

        result shouldBe BigDecimal("50.000000")
    }
}
