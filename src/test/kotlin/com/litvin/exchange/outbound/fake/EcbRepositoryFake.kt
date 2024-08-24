package com.litvin.exchange.outbound.fake

import com.litvin.exchange.domain.repository.EcbRepository
import java.math.BigDecimal

class EcbRepositoryFake : EcbRepository {
    private val datum = mutableMapOf<String, BigDecimal>()

    override fun getSingleEurToCurrencyRate(currency: String): BigDecimal = datum[currency]!!

    override fun getAllEurRates(): Map<String, BigDecimal> = datum

    fun givenRate(
        currencyTo: String,
        rate: BigDecimal,
    ) {
        datum[currencyTo] = rate
    }

    fun clear() {
        datum.clear()
    }
}
