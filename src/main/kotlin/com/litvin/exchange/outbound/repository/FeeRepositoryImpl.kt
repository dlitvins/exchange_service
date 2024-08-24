package com.litvin.exchange.outbound.repository

import com.litvin.exchange.domain.model.Fee
import com.litvin.exchange.domain.repository.FeeRepository
import com.litvin.exchange.outbound.db.FeeDao
import com.litvin.exchange.outbound.db.model.FeeEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
@Transactional
class FeeRepositoryImpl(
    private val feeDao: FeeDao,
) : FeeRepository {
    override fun createOrUpdateFee(fee: Fee): Fee = upsertFee(fee)

    override fun deleteFee(feeId: Long) {
        feeDao.deleteById(feeId)
    }

    override fun getFeeByCurrencies(
        currencyFrom: String,
        currencyTo: String,
    ): Fee? =
        feeDao
            .findFirstByCurrencyFromAndCurrencyTo(
                currencyFrom,
                currencyTo,
            )?.let { toDomain(it) }

    override fun getAllFees(
        currencyFrom: String?,
        currencyTo: String?,
        cursor: Long?,
        limit: Int,
    ): List<Fee> =
        feeDao
            .findAllByCustomQuery(
                currencyFrom = currencyFrom,
                currencyTo = currencyTo,
                cursor = cursor,
                limit = limit,
            ).map { toDomain(it) }

    private fun upsertFee(fee: Fee): Fee {
        val result = feeDao.save(toEntity(fee))
        return toDomain(result)
    }

    private fun toDomain(fee: FeeEntity) =
        Fee(
            id = fee.id,
            currencyFrom = fee.currencyFrom,
            currencyTo = fee.currencyTo,
            value = fee.value,
        )

    private fun toEntity(fee: Fee) =
        FeeEntity(
            id = fee.id,
            currencyFrom = fee.currencyFrom,
            currencyTo = fee.currencyTo,
            value = fee.value,
        )
}
