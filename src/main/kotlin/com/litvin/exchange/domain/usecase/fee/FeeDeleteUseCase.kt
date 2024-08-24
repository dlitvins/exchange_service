package com.litvin.exchange.domain.usecase.fee

import com.litvin.exchange.domain.repository.FeeRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component

interface FeeDeleteUseCase {
    fun deleteFee(
        currencyFrom: String,
        currencyTo: String,
    )
}

private val log = KotlinLogging.logger {}

@Component
class FeeDeleteUseCaseImpl(
    private val feeRepository: FeeRepository,
) : FeeDeleteUseCase {
    override fun deleteFee(
        currencyFrom: String,
        currencyTo: String,
    ) {
        feeRepository.getFeeByCurrencies(currencyFrom, currencyTo)?.id?.let {
            feeRepository.deleteFee(it)
            log.info { "Custom fee for $currencyFrom - $currencyTo exchange removed" }
        }
    }
}
