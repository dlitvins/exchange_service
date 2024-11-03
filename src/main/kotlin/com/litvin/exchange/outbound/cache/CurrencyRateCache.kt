package com.litvin.exchange.outbound.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Service
import java.math.BigDecimal
import kotlin.math.max

interface CurrencyRateCache {
    fun findCurrencyRate(currency: String): BigDecimal?

    fun addCurrencyRate(
        currency: String,
        rate: BigDecimal,
    )

    fun replaceCurrencyRates(rates: Map<String, BigDecimal>)
}

@Service
class CurrencyRateCacheImpl : CurrencyRateCache {
    @Volatile
    private var currencyRateCache: Cache<String, BigDecimal> = createCache(emptyMap())

    override fun findCurrencyRate(currency: String): BigDecimal? = currencyRateCache.getIfPresent(currency)

    override fun addCurrencyRate(
        currency: String,
        rate: BigDecimal,
    ) {
        currencyRateCache.put(currency, rate)
    }

    override fun replaceCurrencyRates(rates: Map<String, BigDecimal>) {
        currencyRateCache = createCache(rates)
    }

    private fun createCache(rates: Map<String, BigDecimal>): Cache<String, BigDecimal> =
        Caffeine
            .newBuilder()
            .initialCapacity(max(rates.size + 10, 200))
            .build<String, BigDecimal>()
            .also { it.putAll(rates) }
}
