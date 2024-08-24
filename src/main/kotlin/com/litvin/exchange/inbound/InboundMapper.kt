package com.litvin.exchange.inbound

import com.litvin.exchange.domain.model.Fee
import com.litvin.exchange.inbound.model.FeeDTO
import com.litvin.exchange.inbound.model.FeePageResponse

object InboundMapper {
    fun toDTO(fee: Fee) =
        FeeDTO(
            currencyFrom = fee.currencyFrom,
            currencyTo = fee.currencyTo,
            value = fee.value,
        )

    fun toDTO(entities: List<Fee>) =
        FeePageResponse(
            items = entities.map { toDTO(it) },
            nextPageToken = entities.lastOrNull()?.id,
        )
}
