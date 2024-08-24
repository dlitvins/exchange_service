package com.litvin.exchange.domain.usecase

import com.litvin.exchange.domain.configuration.StandardFee
import com.litvin.exchange.domain.exception.ExceptionCode
import com.litvin.exchange.domain.exception.UseCaseException
import com.litvin.exchange.domain.repository.FeeRepository
import com.litvin.exchange.outbound.cache.CurrencyRateCache
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max

interface ExchangeCalculatorUseCase {
    fun calculateConversion(
        amount: BigDecimal,
        currencyFrom: String,
        currencyTo: String,
    ): BigDecimal
}

@Component
class ExchangeCalculatorUseCaseImpl(
    private val standardFee: StandardFee,
    private val currencyRateCache: CurrencyRateCache,
    private val feeRepository: FeeRepository,
) : ExchangeCalculatorUseCase {
    override fun calculateConversion(
        amount: BigDecimal,
        currencyFrom: String,
        currencyTo: String,
    ): BigDecimal {
        val resultScale = max(amount.scale() + 3, 6)

        return if (amount.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal.ZERO
        } else if (currencyFrom == currencyTo) {
            amount
        } else {
            val fee = feeRepository.getFeeByCurrencies(currencyFrom, currencyTo)?.value ?: standardFee.value

            val rate =
                getEurExchangeRate(currencyTo).divide(
                    getEurExchangeRate(currencyFrom),
                    resultScale + 1,
                    RoundingMode.HALF_EVEN,
                )

            ((amount - amount * fee) * rate).setScale(resultScale, RoundingMode.HALF_EVEN)
        }
    }

    private fun getEurExchangeRate(currency: String): BigDecimal =
        if (currency == "EUR") {
            BigDecimal.ONE
        } else {
            currencyRateCache.findCurrencyRate(currency)
                ?: throw UseCaseException(ExceptionCode.CURRENCY_NOT_FOUND, "currency $currency not found in cache")
        }
}
