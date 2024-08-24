package com.litvin.exchange.outbound.rest.infrastructure

import feign.Response
import feign.codec.ErrorDecoder
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

open class ConfigurableErrorDecoder(
    private val retryableHttpErrorCodes: List<Int>,
) : ErrorDecoder {
    override fun decode(
        methodKey: String,
        response: Response,
    ): Exception =
        if (retryableHttpErrorCodes.contains(response.status())) {
            log.warn {
                "http status '${response.status()}' is qualified as retryable error; " +
                    "throwing RetryableException to retry the request."
            }
            createRetryableException(response)
        } else {
            createDownStreamException(methodKey, response)
        }
}
