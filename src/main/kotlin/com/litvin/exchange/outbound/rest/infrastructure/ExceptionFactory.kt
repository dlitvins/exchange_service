package com.litvin.exchange.outbound.rest.infrastructure

import com.litvin.exchange.domain.exception.DownStreamServiceException
import com.litvin.exchange.domain.exception.HttpData
import feign.Response
import feign.RetryableException
import kotlin.jvm.optionals.getOrNull

fun createRetryableException(response: Response): RetryableException =
    RetryableException(
        response.status(),
        createErrorMessage(response),
        response.request().httpMethod(),
        null as Long?,
        response.request(),
        response.body().use { stringifyBodyWithLimit(it?.asInputStream()).toByteArray(Charsets.UTF_8) },
        response.headers(),
    )

fun createDownStreamException(
    methodKey: String,
    response: Response,
): DownStreamServiceException =
    DownStreamServiceException(
        serviceName = methodKey,
        status = response.status(),
        message = createErrorMessage(response),
        httpData =
            HttpData(
                requestMethod = response.request().httpMethod().name,
                requestUrl = response.request().url(),
                responseCode = response.status(),
                responseHeaders = stringifyHeaders(response.headers()),
                responseBody = response.body().use { stringifyBodyWithLimit(it?.asInputStream()) },
            ),
    )

fun createDownStreamException(
    serviceName: String,
    attempt: Int,
    retryableException: RetryableException,
): DownStreamServiceException =
    DownStreamServiceException(
        serviceName = serviceName,
        status = retryableException.status(),
        stackTrace = retryableException.stackTraceToString(),
        message = "request failed after $attempt retries: ${retryableException.message}",
        retriesAttempt = attempt,
        httpData =
            HttpData(
                requestMethod = retryableException.request().httpMethod().name,
                requestUrl = retryableException.request().url(),
                responseCode = retryableException.status(),
                responseHeaders = stringifyHeaders(retryableException.responseHeaders()),
                responseBody = stringifyBodyWithLimit(retryableException.responseBody().getOrNull()),
            ),
    )

fun throwDownStreamServiceException(
    serviceName: String,
    attempt: Int,
    retryableException: RetryableException,
) {
    val exception = createDownStreamException(serviceName, attempt, retryableException)
    throw exception
}

private fun createErrorMessage(response: Response): String =
    "HTTP ${response.status()} ${response.request().httpMethod()} ${response.request().url()}"
