package com.litvin.exchange.domain.usecase.fee

import com.litvin.exchange.domain.configuration.StandardFee
import com.litvin.exchange.domain.model.Fee
import com.litvin.exchange.domain.repository.FeeRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component

interface FeeReadUseCase {
    fun getFee(
        currencyFrom: String,
        currencyTo: String,
    ): Fee

    fun getFees(
        currencyFrom: String?,
        currencyTo: String?,
        cursor: Long?,
        limit: Int,
    ): List<Fee>
}

private val log = KotlinLogging.logger {}

@Component
class FeeReadUseCaseImpl(
    private val standardFee: StandardFee,
    private val feeRepository: FeeRepository,
) : FeeReadUseCase {
    override fun getFee(
        currencyFrom: String,
        currencyTo: String,
    ): Fee =
        feeRepository.getFeeByCurrencies(currencyFrom, currencyTo)
            ?: Fee(
                currencyFrom = currencyFrom,
                currencyTo = currencyTo,
                value = standardFee.value,
            )

    override fun getFees(
        currencyFrom: String?,
        currencyTo: String?,
        cursor: Long?,
        limit: Int,
    ): List<Fee> = feeRepository.getAllFees(currencyFrom, currencyTo, cursor, limit)
}
