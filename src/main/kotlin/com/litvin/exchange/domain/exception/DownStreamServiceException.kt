package com.litvin.exchange.domain.exception

open class DownStreamServiceException(
    serviceName: String,
    status: Int,
    stackTrace: String? = null,
    message: String,
    retriesAttempt: Int = 0,
    httpData: HttpData? = null,
    override val errorDetails: DownStreamServiceErrorDetails =
        DownStreamServiceErrorDetails(
            serviceName = serviceName,
            status = status,
            message = message,
            stackTrace = stackTrace,
            retriesAttempt = retriesAttempt,
            httpData = httpData,
        ),
) : UseCaseException(
        code = ExceptionCode.DOWNSTREAM_SERVICE_ERROR,
        errorDetails = errorDetails,
    )

data class DownStreamServiceErrorDetails(
    val serviceName: String,
    val status: Int,
    val message: String?,
    val stackTrace: String?,
    val retriesAttempt: Int = 0,
    val httpData: HttpData? = null,
)

data class HttpData(
    val requestMethod: String,
    val requestUrl: String,
    val responseCode: Int,
    val responseHeaders: Map<String, String>,
    val responseBody: String,
)
