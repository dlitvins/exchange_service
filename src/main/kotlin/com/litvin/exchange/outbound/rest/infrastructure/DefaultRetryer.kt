package com.litvin.exchange.outbound.rest.infrastructure

import feign.RetryableException
import feign.Retryer
import mu.KotlinLogging
import kotlin.math.pow

private val log = KotlinLogging.logger {}

open class DefaultRetryer(
    private val serviceName: String,
    private val retryMaxAttempt: Int = 5,
    private val initialDelayInMillis: Int = 100,
    private val backoffFactor: Double = 1.5,
) : Retryer {
    protected var attempt = 0

    override fun continueOrPropagate(retryableException: RetryableException) {
        if (attempt >= retryMaxAttempt) {
            throwDownStreamServiceException(serviceName, attempt, retryableException)
        }

        val backOffDelay: Long = calculateSleepIntervalInMillis()
        log.warn {
            "feign retry will be applied ${attempt + 1}/$retryMaxAttempt; " +
                "after backoff delay $backOffDelay millis; error details: '${retryableException.message}'."
        }
        Thread.sleep(backOffDelay)

        attempt++
    }

    override fun clone(): Retryer =
        DefaultRetryer(
            serviceName = serviceName,
            retryMaxAttempt = retryMaxAttempt,
            initialDelayInMillis = initialDelayInMillis,
            backoffFactor = backoffFactor,
        )

    protected fun calculateSleepIntervalInMillis(): Long = (initialDelayInMillis * backoffFactor.pow(attempt)).toLong()
}
