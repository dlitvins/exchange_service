package com.litvin.exchange.domain.repository

import com.litvin.exchange.domain.model.Fee

interface FeeRepository {
    fun createOrUpdateFee(fee: Fee): Fee

    fun deleteFee(feeId: Long)

    fun getFeeByCurrencies(
        currencyFrom: String,
        currencyTo: String,
    ): Fee?

    fun getAllFees(
        currencyFrom: String?,
        currencyTo: String?,
        cursor: Long?,
        limit: Int,
    ): List<Fee>
}
