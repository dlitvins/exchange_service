package com.litvin.exchange.domain.usecase.exchangeCalculator

import com.litvin.exchange.domain.configuration.StandardFee
import com.litvin.exchange.domain.usecase.ExchangeCalculatorUseCaseImpl
import com.litvin.exchange.outbound.fake.CurrencyRateCacheFake
import com.litvin.exchange.outbound.fake.FeeRepositoryFake
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ExchangeCalculatorUseCaseImplTestKit(
    private val standardFee: BigDecimal,
) {
    protected lateinit var sut: ExchangeCalculatorUseCaseImpl
    protected val feeRepositoryFake = FeeRepositoryFake()
    protected val currencyRateCache = CurrencyRateCacheFake()
    private val fee = StandardFee(standardFee)

    @BeforeAll
    fun setup() {
        this.sut =
            ExchangeCalculatorUseCaseImpl(
                feeRepository = feeRepositoryFake,
                currencyRateCache = currencyRateCache,
                standardFee = fee,
            )
    }

    @BeforeEach
    fun clear() {
        currencyRateCache.clear()
        feeRepositoryFake.clear()
    }

    @Test
    fun `when calculation from to same currency then return amount unchanged`() {
        val result = sut.calculateConversion(BigDecimal("67"), "USD", "USD")

        result shouldBe BigDecimal("67")
    }

    @Test
    fun `when amount is zero then return amount unchanged`() {
        val result = sut.calculateConversion(BigDecimal("0.000"), "EUR", "USD")

        result shouldBe BigDecimal("0")
    }
}
