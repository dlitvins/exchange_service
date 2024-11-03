package com.litvin.exchange.learning

import com.litvin.exchange.domain.configuration.StandardFee
import com.litvin.exchange.domain.exception.UseCaseException
import com.litvin.exchange.domain.usecase.CurrencyRateCacheUpdateUseCaseImpl
import com.litvin.exchange.domain.usecase.ExchangeCalculatorUseCaseImpl
import com.litvin.exchange.outbound.cache.CurrencyRatesCacheWarmUpService
import com.litvin.exchange.outbound.fake.CurrencyRateCacheFake
import com.litvin.exchange.outbound.fake.EcbRepositoryFake
import com.litvin.exchange.outbound.fake.FeeRepositoryFake
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConcurrentCacheUpdateTest {
    private val feeRepositoryFake = FeeRepositoryFake()
    private val currencyRateCache = CurrencyRateCacheFake()
    private val fee = StandardFee(BigDecimal.ZERO)

    private val ecbRepositoryFake = EcbRepositoryFake()
    private val currencyRatesCacheWarmUpService =
        spyk(
            CurrencyRatesCacheWarmUpService(
                currencyRateCache = currencyRateCache,
                ecbRepository = ecbRepositoryFake,
            ),
        )

    private val updateUseCase =
        CurrencyRateCacheUpdateUseCaseImpl(
            currencyRateCache = currencyRateCache,
            currencyRatesCacheWarmUpService = currencyRatesCacheWarmUpService,
        )

    private val sut: ExchangeCalculatorUseCaseImpl =
        ExchangeCalculatorUseCaseImpl(
            feeRepository = feeRepositoryFake,
            currencyRateCache = currencyRateCache,
            standardFee = fee,
        )

    @BeforeAll
    fun setup() {
        this.sut
    }

    @BeforeEach
    fun init() {
        feeRepositoryFake.clear()
        currencyRateCache.clear()
        ecbRepositoryFake.clear()
    }

    @Test
    fun `when cache were update before calculation then return correct value`() {
        currencyRateCache.addCurrencyRate("USD", BigDecimal("4"))
        currencyRateCache.addCurrencyRate("AUD", BigDecimal("2"))

        sut.calculateConversion(BigDecimal("200"), "USD", "AUD") shouldBe BigDecimal("100.000000")

        ecbRepositoryFake.givenRate("USD", BigDecimal("8"))
        ecbRepositoryFake.givenRate("AUD", BigDecimal("2"))

        updateUseCase.resetCache()

        sut.calculateConversion(BigDecimal("200"), "USD", "AUD") shouldBe BigDecimal("50.000000")
    }

    @Test
    fun `when cache were not update before calculation then throw exception`() {
        currencyRateCache.addCurrencyRate("USD", BigDecimal("4"))
        currencyRateCache.addCurrencyRate("AUD", BigDecimal("2"))

        sut.calculateConversion(BigDecimal("200"), "USD", "AUD") shouldBe BigDecimal("100.000000")

        ecbRepositoryFake.givenRate("USD", BigDecimal("8"))
        ecbRepositoryFake.givenRate("AUD", BigDecimal("2"))

        every { currencyRatesCacheWarmUpService.warmUpCurrencyRateCache() } answers {
            runBlocking {
                launch {
                    delay(200)
                    callOriginal()
                }
            }
        }

        runBlocking {
            launch { updateUseCase.resetCache() }
            launch {
                shouldThrow<UseCaseException> { sut.calculateConversion(BigDecimal("200"), "USD", "AUD") }
            }
        }
    }
}
