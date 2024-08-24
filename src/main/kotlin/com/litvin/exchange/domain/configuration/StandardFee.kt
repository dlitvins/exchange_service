package com.litvin.exchange.domain.configuration

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StandardFee(
    @Value("\${application.standard-fee}") val value: BigDecimal,
) {
    @PostConstruct
    fun validate() {
        if (value !in BigDecimal.ZERO..BigDecimal.ONE) {
            throw RuntimeException("Incorrect standard fee values")
        }
    }
}
