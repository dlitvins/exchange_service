package com.litvin.exchange.domain.exception

open class UseCaseException(
    val code: ExceptionCode,
    open val errorDetails: Any,
) : RuntimeException("code: $code, errorDetails: $errorDetails")
