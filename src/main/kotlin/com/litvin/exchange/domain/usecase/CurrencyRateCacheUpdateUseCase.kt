package com.litvin.exchange.domain.usecase

import com.litvin.exchange.domain.repository.EcbRepository
import com.litvin.exchange.outbound.cache.CurrencyRateCache
import org.springframework.stereotype.Component

interface CurrencyRateCacheUpdateUseCase {
    fun resetCache()
}

@Component
class CurrencyRateCacheUpdateUseCaseImpl(
    private val currencyRateCache: CurrencyRateCache,
    private val ecbRepository: EcbRepository,
) : CurrencyRateCacheUpdateUseCase {
    override fun resetCache() {
        val rates = ecbRepository.getAllEurRates()
        currencyRateCache.replaceCurrencyRates(rates)
    }
}
