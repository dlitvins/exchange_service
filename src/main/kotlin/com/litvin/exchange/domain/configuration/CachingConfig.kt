package com.litvin.exchange.domain.configuration

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
@EnableCaching
class CachingConfig {
    @Primary
    @Bean("caffeineCacheManager")
    fun caffeineCacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()
        return cacheManager
    }
}
