package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.exception.ExceptionCode.INVALID_REQUEST
import com.litvin.exchange.domain.exception.ExceptionCode.VALIDATION_ERROR
import com.litvin.exchange.domain.exception.UseCaseException
import com.litvin.exchange.domain.exception.ValidationError
import com.litvin.exchange.domain.usecase.fee.FeeReadUseCase
import com.litvin.exchange.inbound.InboundMapper.toDTO
import com.litvin.exchange.inbound.model.FeeDTO
import com.litvin.exchange.inbound.model.FeePageResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Fee Management")
@RestController
@RequestMapping("/fee")
class FeeReadController(
    private val feeReadUseCase: FeeReadUseCase,
) {
    @GetMapping
    fun getFee(
        @RequestParam currencyFrom: String,
        @RequestParam currencyTo: String,
    ): FeeDTO = toDTO(feeReadUseCase.getFee(currencyFrom, currencyTo))

    @GetMapping("/list")
    fun getFees(
        @RequestParam(required = false) currencyFrom: String?,
        @RequestParam(required = false) currencyTo: String?,
        @RequestParam(required = false) cursor: Long? = null,
        @RequestParam(required = false) limit: Int = 50,
    ): FeePageResponse {
        validateLimit(limit)
        return toDTO(feeReadUseCase.getFees(currencyFrom, currencyTo, cursor, limit))
    }

    private fun validateLimit(limit: Int) {
        val validationErrors = mutableListOf<ValidationError>()
        if (limit < 1) {
            validationErrors.add(ValidationError(VALIDATION_ERROR, "Invalid limit - min value is 1"))
        }
        if (limit > 1000) {
            validationErrors.add(ValidationError(VALIDATION_ERROR, "Invalid limit - max value is 1000"))
        }
        if (validationErrors.isNotEmpty()) {
            throw UseCaseException(INVALID_REQUEST, validationErrors.toString())
        }
    }
}
