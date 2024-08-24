package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.usecase.fee.FeeDeleteUseCase
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Fee Management")
@RestController
@RequestMapping("/fee")
class FeeDeleteController(
    private val feeDeleteUseCase: FeeDeleteUseCase,
) {
    @DeleteMapping
    fun deleteFee(
        @RequestParam currencyFrom: String,
        @RequestParam currencyTo: String,
    ) {
        feeDeleteUseCase.deleteFee(currencyFrom, currencyTo)
    }
}
