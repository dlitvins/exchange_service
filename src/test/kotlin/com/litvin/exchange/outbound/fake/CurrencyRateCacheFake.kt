package com.litvin.exchange.outbound.fake

import com.litvin.exchange.outbound.cache.CurrencyRateCache
import java.math.BigDecimal

class CurrencyRateCacheFake : CurrencyRateCache {
    private val datum = mutableMapOf<String, BigDecimal>()

    override fun findCurrencyRate(currency: String): BigDecimal? = datum[currency]

    override fun addCurrencyRate(
        currency: String,
        rate: BigDecimal,
    ) {
        datum[currency] = rate
    }

    override fun clear() {
        datum.clear()
    }

    fun reset() {
        datum.clear()
        addCurrencyRate("ARS", BigDecimal("91.5953"))
        addCurrencyRate("USD", BigDecimal("1.1121"))
        addCurrencyRate("ZAR", BigDecimal("19.9591"))
    }
}
