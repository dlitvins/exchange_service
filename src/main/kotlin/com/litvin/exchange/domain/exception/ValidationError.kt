package com.litvin.exchange.domain.exception

data class ValidationError(
    val code: ExceptionCode,
    val errorDetails: Any,
)
