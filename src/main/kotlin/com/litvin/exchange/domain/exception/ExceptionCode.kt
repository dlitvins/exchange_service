package com.litvin.exchange.domain.exception

enum class ExceptionCode {
    UNAUTHORIZED,

    INVALID_REQUEST,
    INTERNAL_ERROR,
    DOWNSTREAM_SERVICE_ERROR,

    CONCURRENT_MODIFICATION,

    VALIDATION_ERROR,

    CURRENCY_NOT_FOUND,
}
