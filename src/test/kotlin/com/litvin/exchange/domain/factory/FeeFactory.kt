package com.litvin.exchange.domain.factory

import com.litvin.exchange.domain.model.Fee
import java.math.BigDecimal

object FeeFactory {
    fun givenFee(
        id: Long? = null,
        currencyTo: String = "ARS",
        currencyFrom: String = "USD",
        value: BigDecimal = BigDecimal("0.3"),
    ) = Fee(id, currencyTo, currencyFrom, value)
}
