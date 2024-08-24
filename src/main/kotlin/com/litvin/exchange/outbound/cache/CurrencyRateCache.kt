package com.litvin.exchange.outbound.cache

import com.litvin.exchange.domain.configuration.CacheRegion
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.support.NullValue
import org.springframework.stereotype.Service
import java.math.BigDecimal

interface CurrencyRateCache {
    fun findCurrencyRate(currency: String): BigDecimal?

    fun addCurrencyRate(
        currency: String,
        rate: BigDecimal,
    )

    fun clear()
}

@Service
class CurrencyRateCacheImpl(
    cacheManager: CacheManager,
) : CurrencyRateCache {
    private val currencyRateCache: Cache =
        cacheManager.getCache(CacheRegion.CURRENCY_RATES.name) ?: throw RuntimeException("Currency cache not set")

    override fun findCurrencyRate(currency: String): BigDecimal? =
        currencyRateCache[currency]?.get()?.let { if (it is NullValue) return null else it as BigDecimal }

    override fun addCurrencyRate(
        currency: String,
        rate: BigDecimal,
    ) {
        currencyRateCache.putIfAbsent(currency, rate)
    }

    override fun clear() {
        currencyRateCache.clear()
    }
}
