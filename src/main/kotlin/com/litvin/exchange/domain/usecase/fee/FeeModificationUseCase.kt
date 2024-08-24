package com.litvin.exchange.domain.usecase.fee

import com.litvin.exchange.domain.model.Fee
import com.litvin.exchange.domain.repository.FeeRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

interface FeeModificationUseCase {
    fun createOrUpdateFee(
        currencyFrom: String,
        currencyTo: String,
        fee: BigDecimal,
    ): Fee
}

@Component
class FeeModificationUseCaseImpl(
    private val feeRepository: FeeRepository,
) : FeeModificationUseCase {
    override fun createOrUpdateFee(
        currencyFrom: String,
        currencyTo: String,
        fee: BigDecimal,
    ): Fee {
        val persistedFee = feeRepository.getFeeByCurrencies(currencyFrom, currencyTo)
        return feeRepository.createOrUpdateFee(
            Fee(
                id = persistedFee?.id,
                currencyFrom = currencyFrom,
                currencyTo = currencyTo,
                value = fee,
            ),
        )
    }
}
