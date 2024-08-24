package com.litvin.exchange.domain.repository

import java.math.BigDecimal

interface EcbRepository {
    fun getSingleEurToCurrencyRate(currency: String): BigDecimal

    fun getAllEurRates(): Map<String, BigDecimal>
}
