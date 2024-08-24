package com.litvin.exchange.domain.usecase

import com.litvin.exchange.outbound.cache.CurrencyRateCache
import com.litvin.exchange.outbound.cache.CurrencyRatesCacheWarmUpService
import org.springframework.stereotype.Component

interface CurrencyRateCacheUpdateUseCase {
    fun resetCache()
}

@Component
class CurrencyRateCacheUpdateUseCaseImpl(
    private val currencyRateCache: CurrencyRateCache,
    private val currencyRatesCacheWarmUpService: CurrencyRatesCacheWarmUpService,
) : CurrencyRateCacheUpdateUseCase {
    override fun resetCache() {
        currencyRateCache.clear()
        currencyRatesCacheWarmUpService.warmUpCurrencyRateCache()
    }
}
