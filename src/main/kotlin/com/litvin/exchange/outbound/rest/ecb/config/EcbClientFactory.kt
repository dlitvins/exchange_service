package com.litvin.exchange.outbound.rest.ecb.config

import com.litvin.exchange.outbound.rest.ecb.EcbClient
import com.litvin.exchange.outbound.rest.infrastructure.ConfigurableErrorDecoder
import com.litvin.exchange.outbound.rest.infrastructure.DefaultRetryer
import com.litvin.exchange.outbound.rest.infrastructure.FeignSlf4jLogger
import com.litvin.exchange.outbound.rest.infrastructure.defaultFeignApacheClient5
import com.litvin.exchange.outbound.rest.infrastructure.newDefaultJsonDecoder
import com.litvin.exchange.outbound.rest.infrastructure.newDefaultJsonEncoder
import feign.Contract
import feign.Feign
import feign.Logger
import feign.Request
import feign.RequestInterceptor
import feign.Retryer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class EcbClientConfig(
    @Value("\${outbound.ecb.url}") val url: String,
)

@Component
class EcbClientFactory(
    private val config: EcbClientConfig,
) {
    private val connectTimeoutMillis = 30_000L
    private val readTimeoutMillis = 90_000L

    @Bean
    fun ecbClient(): EcbClient =
        Feign
            .builder()
            .options(requestOptions())
            .contract(useFeignAnnotations())
            .encoder(newDefaultJsonEncoder())
            .decoder(newDefaultJsonDecoder())
            .errorDecoder(errorDecoder())
            .retryer(retryer())
            .logger(FeignSlf4jLogger(EcbClient::class.java))
            .logLevel(feignLogLevel())
            .client(defaultFeignApacheClient5())
            .requestInterceptors(requestInterceptors())
            .target(EcbClient::class.java, config.url)

    private fun errorDecoder() =
        ConfigurableErrorDecoder(
            buildList { addAll(500..599) },
        )

    private fun feignLogLevel() = Logger.Level.BASIC

    private fun useFeignAnnotations() = Contract.Default()

    private fun requestInterceptors() = emptyList<RequestInterceptor>()

    private fun requestOptions() =
        Request.Options(
            connectTimeoutMillis,
            TimeUnit.MILLISECONDS,
            readTimeoutMillis,
            TimeUnit.MILLISECONDS,
            false,
        )

    private fun retryer(): Retryer =
        DefaultRetryer(
            serviceName = "ECB",
            retryMaxAttempt = 5,
            initialDelayInMillis = 200,
            backoffFactor = 2.0,
        )
}
