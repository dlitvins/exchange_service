package com.litvin.exchange.domain.configuration

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

enum class CacheRegion {
    CURRENCY_RATES,
}

@Configuration
@EnableCaching
class CachingConfig {
    private val customConfiguration: Map<CacheRegion, Caffeine<Any, Any>> =
        mapOf(
            CacheRegion.CURRENCY_RATES to
                Caffeine
                    .newBuilder()
                    .initialCapacity(2000),
        )

    @Primary
    @Bean("caffeineCacheManager")
    fun caffeineCacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()
        CacheRegion
            .entries
            .forEach { cacheName ->
                val caffeineConfig: Caffeine<Any, Any> = getConfigByNameOrDefault(cacheName)
                caffeineConfig.recordStats()
                cacheManager.registerCustomCache(cacheName.name, caffeineConfig.build())
            }

        return cacheManager
    }

    private fun getConfigByNameOrDefault(cacheName: CacheRegion): Caffeine<Any, Any> =
        customConfiguration[cacheName] ?: Caffeine.newBuilder()
}
