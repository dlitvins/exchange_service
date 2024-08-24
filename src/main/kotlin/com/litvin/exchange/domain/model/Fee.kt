package com.litvin.exchange.domain.model

import java.math.BigDecimal

data class Fee(
    val id: Long? = null,
    val currencyFrom: String,
    val currencyTo: String,
    val value: BigDecimal,
)
