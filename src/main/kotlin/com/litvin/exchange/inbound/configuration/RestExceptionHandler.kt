package com.litvin.exchange.inbound.configuration

import com.litvin.exchange.domain.exception.ExceptionCode
import com.litvin.exchange.domain.exception.UseCaseException
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

private val log = KotlinLogging.logger {}

@ControllerAdvice("com.litvin")
class RestExceptionHandler {
    private val userIdKey = "userId"
    private val traceIdKey = "traceId"

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ErrorDocument> {
        val errorResponse =
            ErrorDocument(
                code = "UNEXPECTED_EXCEPTION",
                errorDetails = ex.message ?: "",
                requestId = MDC.get(traceIdKey),
                userId = MDC.get(userIdKey),
            )
        log.error { ex }
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(UseCaseException::class)
    fun handleUseCaseException(exception: UseCaseException): ResponseEntity<ErrorDocument> {
        val errorDocument =
            ErrorDocument(
                code = exception.code.name,
                errorDetails = exception.errorDetails,
                requestId = MDC.get(traceIdKey),
                userId = MDC.get(userIdKey),
            )
        log.error { "$exception: code (${exception.code.name}) errorDetails (${exception.errorDetails})" }
        return ResponseEntity(errorDocument, mapExceptionCodeToStatus(exception.code))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(exception: MethodArgumentTypeMismatchException): ResponseEntity<ErrorDocument> {
        var errorDetails = exception.message ?: ""
        if (exception.requiredType?.isEnum == true) {
            val enumConstants = exception.requiredType?.enumConstants?.map { it.toString() }
            errorDetails =
                "Invalid value '${exception.value}' for '${exception.name}' parameter, expected values: $enumConstants"
        }
        return handleUseCaseException(UseCaseException(ExceptionCode.INVALID_REQUEST, errorDetails))
    }
}

private fun mapExceptionCodeToStatus(exceptionCode: ExceptionCode): HttpStatus =
    when (exceptionCode) {
        ExceptionCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
        ExceptionCode.DOWNSTREAM_SERVICE_ERROR -> HttpStatus.FAILED_DEPENDENCY
        ExceptionCode.INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR
        ExceptionCode.INVALID_REQUEST -> HttpStatus.BAD_REQUEST
        ExceptionCode.VALIDATION_ERROR -> HttpStatus.BAD_REQUEST
        ExceptionCode.CONCURRENT_MODIFICATION -> HttpStatus.CONFLICT
        ExceptionCode.CURRENCY_NOT_FOUND -> HttpStatus.NOT_FOUND
    }

data class ErrorDocument(
    val code: String,
    val errorDetails: Any,
    val requestId: String?,
    val userId: String?,
)
