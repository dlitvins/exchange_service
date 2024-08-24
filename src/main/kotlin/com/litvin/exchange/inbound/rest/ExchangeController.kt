package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.exception.ExceptionCode.INVALID_REQUEST
import com.litvin.exchange.domain.exception.ExceptionCode.VALIDATION_ERROR
import com.litvin.exchange.domain.exception.UseCaseException
import com.litvin.exchange.domain.exception.ValidationError
import com.litvin.exchange.domain.usecase.ExchangeCalculatorUseCase
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@Tag(name = "Exchange Calculator")
@RestController
@RequestMapping("/exchange")
class ExchangeController(
    private val exchangeCalculatorUseCase: ExchangeCalculatorUseCase,
) {
    @PostMapping
    fun calculateExchange(
        @RequestBody request: ExchangeRequest,
    ): ExchangeResponse {
        validateRequest(request)
        val result =
            exchangeCalculatorUseCase.calculateConversion(
                request.amount,
                request.currencyFrom.uppercase(),
                request.currencyTo.uppercase(),
            )
        return ExchangeResponse(result)
    }

    private fun validateRequest(request: ExchangeRequest) {
        val validationErrors = mutableListOf<ValidationError>()
        if (request.amount < BigDecimal.ZERO) {
            validationErrors.add(ValidationError(VALIDATION_ERROR, "Invalid amount"))
        }
        if (request.currencyFrom.isBlank()) {
            validationErrors.add(ValidationError(VALIDATION_ERROR, "Invalid from currency"))
        }
        if (request.currencyTo.isBlank()) {
            validationErrors.add(ValidationError(VALIDATION_ERROR, "Invalid to currency"))
        }
        if (validationErrors.isNotEmpty()) {
            throw UseCaseException(INVALID_REQUEST, validationErrors.toString())
        }
    }

    data class ExchangeRequest(
        val amount: BigDecimal,
        val currencyFrom: String,
        val currencyTo: String,
    )

    data class ExchangeResponse(
        val resultAmount: BigDecimal,
    )
}
