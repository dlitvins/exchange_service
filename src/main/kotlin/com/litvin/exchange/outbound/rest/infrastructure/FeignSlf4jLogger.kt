package com.litvin.exchange.outbound.rest.infrastructure

import feign.Response
import feign.Util
import feign.slf4j.Slf4jLogger
import mu.KotlinLogging
import java.util.StringJoiner

private const val MAX_PAYLOAD_SIZE = 500

class FeignSlf4jLogger(
    clazz: Class<*>,
) : Slf4jLogger(clazz) {
    private val logger = KotlinLogging.logger(clazz.canonicalName)

    override fun logAndRebufferResponse(
        configKey: String,
        logLevel: Level,
        response: Response,
        elapsedTime: Long,
    ): Response {
        val responseIsOk = response.status() in 200..299
        return if (responseIsOk) {
            logSuccessfulResponse(configKey, logLevel, response, elapsedTime)
        } else {
            logFailedResponse(configKey, response, elapsedTime)
        }
    }

    private fun logSuccessfulResponse(
        configKey: String,
        logLevel: Level,
        response: Response,
        elapsedTime: Long,
    ): Response {
        val protocolVersion = resolveProtocolVersion(response.protocolVersion())
        val status = response.status()
        val method = response.request().httpMethod().name
        val url = response.request().url()

        log(configKey, "<--- $protocolVersion $status $method $url (${elapsedTime}ms)")

        if (logLevel.ordinal >= Level.HEADERS.ordinal) {
            for (field in response.headers().keys) {
                if (shouldLogResponseHeader(field)) {
                    for (value in response.headers()[field].orEmpty()) {
                        log(configKey, "$field: $value")
                    }
                }
            }

            if (response.body() != null && !(status == 204 || status == 205)) {
                // HTTP 204 No Content "...response MUST NOT include a message body"
                // HTTP 205 Reset Content "...response MUST NOT include an entity"
                if (logLevel.ordinal >= Level.FULL.ordinal) {
                    log(configKey, "")
                }

                val bodyData = response.body().asInputStream().use { Util.toByteArray(it) }
                val bodyLength: Int = bodyData.size
                if (logLevel.ordinal >= Level.FULL.ordinal && bodyLength > 0) {
                    log(configKey, "%s", Util.decodeOrDefault(bodyData, Util.UTF_8, "Binary data"))
                }

                log(configKey, "<--- END HTTP ($bodyLength-byte body)")
                return response.toBuilder().body(bodyData).build()
            } else {
                log(configKey, "<--- END HTTP (0-byte body)")
            }
        }
        return response
    }

    private fun logFailedResponse(
        configKey: String,
        response: Response,
        elapsedTime: Long,
    ): Response {
        val protocolVersion = resolveProtocolVersion(response.protocolVersion())
        val status = response.status()
        val url = response.request().url()
        val method = response.request().httpMethod().name
        val accumulatedMessage = StringJoiner("\n")
        accumulatedMessage.add("${methodTag(configKey)}<--- $protocolVersion $status $method $url ${elapsedTime}ms")

        for (field in response.headers().keys) {
            for (value in response.headers()[field].orEmpty()) {
                accumulatedMessage.add("$field: $value")
            }
        }

        if (response.body() != null && !(status == 204 || status == 205)) {
            // HTTP 204 No Content "...response MUST NOT include a message body"
            // HTTP 205 Reset Content "...response MUST NOT include an entity"
            accumulatedMessage.add("")
            val bodyData = response.body().asInputStream().use { Util.toByteArray(it) }
            val bodyLength: Int = bodyData.size
            if (bodyLength > 0) {
                val data: String = Util.decodeOrDefault(bodyData, Util.UTF_8, "Binary data")
                val payloadSnippet: String = extractPayloadSnippetOrFull(data)
                accumulatedMessage.add(payloadSnippet)
            }
            accumulatedMessage.add("<--- END HTTP ($bodyLength-byte body)")
            logger.error { accumulatedMessage.toString() }
            return response.toBuilder().body(bodyData).build()
        } else {
            accumulatedMessage.add("<--- END HTTP (0-byte body)")
        }
        logger.error { accumulatedMessage.toString() }
        return response
    }

    private fun extractPayloadSnippetOrFull(payload: String): String =
        if (payload.length > MAX_PAYLOAD_SIZE) "${payload.take(MAX_PAYLOAD_SIZE)}..." else payload
}
