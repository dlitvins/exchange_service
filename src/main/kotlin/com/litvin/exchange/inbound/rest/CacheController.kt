package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.usecase.CurrencyRateCacheUpdateUseCase
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Currency Rate Cache")
@RestController
@RequestMapping("/cache/currency-rate")
class CacheController(
    private val currencyRateCacheUpdateUseCase: CurrencyRateCacheUpdateUseCase,
) {
    @PostMapping("/reset")
    fun resetCache() {
        currencyRateCacheUpdateUseCase.resetCache()
    }
}
