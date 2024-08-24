package com.litvin.exchange.outbound.fake

import com.litvin.exchange.domain.model.Fee
import com.litvin.exchange.domain.repository.FeeRepository
import java.util.UUID

class FeeRepositoryFake : FeeRepository {
    private val datum = mutableMapOf<Long, Fee>()

    override fun createOrUpdateFee(fee: Fee): Fee {
        val persisted: Fee? =
            datum.values.firstOrNull { it.currencyFrom == fee.currencyFrom && it.currencyTo == fee.currencyTo }
        val id = persisted?.id ?: (UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE)

        return fee.copy(id = id).let {
            datum[id] = it
            it
        }
    }

    override fun deleteFee(feeId: Long) {
        datum.remove(feeId)
    }

    override fun getFeeByCurrencies(
        currencyFrom: String,
        currencyTo: String,
    ): Fee? = datum.values.firstOrNull { it.currencyFrom == currencyFrom && it.currencyTo == currencyTo }

    override fun getAllFees(
        currencyFrom: String?,
        currencyTo: String?,
        cursor: Long?,
        limit: Int,
    ): List<Fee> =
        datum.values
            .asSequence()
            .filter { fee -> currencyFrom?.let { fee.currencyFrom == it } ?: true }
            .filter { fee -> currencyTo?.let { fee.currencyTo == it } ?: true }
            .sortedBy { it.id }
            .filter { fee -> cursor?.let { fee.id!! > it } ?: true }
            .take(limit)
            .toList()

    fun findAll() = datum.values.toList()

    fun givenFee(fee: Fee) {
        datum[fee.id!!] = fee
    }

    fun clear() {
        datum.clear()
    }
}
