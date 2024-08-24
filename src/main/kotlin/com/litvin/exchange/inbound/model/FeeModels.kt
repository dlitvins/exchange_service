package com.litvin.exchange.inbound.model

import java.math.BigDecimal

data class FeeDTO(
    val currencyFrom: String,
    val currencyTo: String,
    val value: BigDecimal,
)

data class FeePageResponse(
    override val items: List<FeeDTO>,
    override val nextPageToken: Long?,
) : PageObjectResponse<FeeDTO, Long>
