package com.litvin.exchange.outbound.cache

import com.litvin.exchange.domain.repository.EcbRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class CurrencyRatesCacheWarmUpService(
    private val ecbRepository: EcbRepository,
    private val currencyRateCache: CurrencyRateCache,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun warmUpCurrencyRateCache() {
        ecbRepository.getAllEurRates().forEach {
            currencyRateCache.addCurrencyRate(it.key, it.value)
        }
    }
}
