package com.litvin.exchange.learning

import com.github.benmanes.caffeine.cache.Caffeine
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ConcurrentCacheUpdateTest {
    @Test
    fun `when get rate after update should return value`() {
        // Cache Warm Up - 1st Use Case
        val cache = Caffeine.newBuilder().build<String, BigDecimal>()
        cache.putAll(
            mapOf(
                "USD" to BigDecimal("4"),
            ),
        )
        cache.getIfPresent("USD") shouldBe BigDecimal("4")

        // Cache Update Use Case - First step
        cache.cleanUp()
        // Cache Update Use Case - Second step
        cache.putAll(
            mapOf(
                "USD" to BigDecimal("3"),
            ),
        )

        // Calculation Use Case - all updated before execution
        cache.getIfPresent("AUD") shouldBe BigDecimal("3")
    }

    @Test
    fun `when get rate before update should return null`() {
        // Cache Warm Up - 1st Use Case
        val cache = Caffeine.newBuilder().build<String, BigDecimal>()
        cache.putAll(
            mapOf(
                "USD" to BigDecimal("4"),
            ),
        )
        cache.getIfPresent("USD") shouldBe BigDecimal("4")

        // Cache Update Use Case - First step
        cache.cleanUp()

        // Calculation Use Case - not updated yet
        cache.getIfPresent("AUD") shouldBe null

        // Cache Update Use Case - Second step
        cache.putAll(
            mapOf(
                "USD" to BigDecimal("3"),
            ),
        )

        // Calculation Use Case - all updated before execution
        cache.getIfPresent("AUD") shouldBe BigDecimal("3")
    }
}
