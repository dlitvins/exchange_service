package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.exception.ExceptionCode.INVALID_REQUEST
import com.litvin.exchange.domain.exception.ExceptionCode.VALIDATION_ERROR
import com.litvin.exchange.domain.exception.UseCaseException
import com.litvin.exchange.domain.exception.ValidationError
import com.litvin.exchange.domain.usecase.fee.FeeModificationUseCase
import com.litvin.exchange.inbound.InboundMapper.toDTO
import com.litvin.exchange.inbound.model.FeeDTO
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@Tag(name = "Fee Management")
@RestController
@RequestMapping("/fee")
class FeeModificationController(
    private val feeModificationUseCase: FeeModificationUseCase,
) {
    @PostMapping
    fun createOrUpdateFee(
        @RequestBody request: FeeDTO,
    ): FeeDTO {
        val validatedRequest = validateRequest(request)
        val result =
            feeModificationUseCase.createOrUpdateFee(
                validatedRequest.currencyFrom,
                validatedRequest.currencyTo,
                validatedRequest.value,
            )
        return toDTO(result)
    }

    private fun validateRequest(request: FeeDTO): FeeDTO {
        val currencyFrom = request.currencyFrom.uppercase().trim()
        val currencyTo = request.currencyTo.uppercase().trim()

        val validationErrors = mutableListOf<ValidationError>()
        if (request.value !in BigDecimal.ZERO..BigDecimal.ONE || request.value.scale() > 5) {
            validationErrors.add(
                ValidationError(
                    VALIDATION_ERROR,
                    "Invalid amount." +
                        "Should be between 0 and 1 and 5 significant digits after comma.",
                ),
            )
        }
        if (currencyFrom.isBlank() || currencyFrom.length > 10) {
            validationErrors.add(
                ValidationError(
                    VALIDATION_ERROR,
                    "Invalid from currency. Type currency in ISO format.",
                ),
            )
        }
        if (currencyTo.isBlank() || currencyTo.length > 10) {
            validationErrors.add(ValidationError(VALIDATION_ERROR, "Invalid to currency. Type currency in ISO format."))
        }
        if (currencyFrom == currencyTo) {
            validationErrors.add(ValidationError(VALIDATION_ERROR, "Same from to currency"))
        }
        if (validationErrors.isNotEmpty()) {
            throw UseCaseException(INVALID_REQUEST, validationErrors.toString())
        }

        return FeeDTO(currencyFrom, currencyTo, value = request.value)
    }
}
