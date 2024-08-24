package com.litvin.exchange.outbound.rest.infrastructure

import com.litvin.exchange.EMPTY_STRING
import io.micrometer.core.instrument.util.IOUtils
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

fun stringifyBodyWithLimit(responseBuffer: ByteBuffer?): String {
    if (responseBuffer == null) {
        return EMPTY_STRING
    }
    val str: String = StandardCharsets.UTF_8.decode(responseBuffer).toString()
    return takePayloadChunk(str)
}

fun stringifyBodyWithLimit(responseStream: InputStream?): String {
    if (responseStream == null) {
        return EMPTY_STRING
    }
    val str: String = IOUtils.toString(responseStream, StandardCharsets.UTF_8)
    return takePayloadChunk(str)
}

private fun takePayloadChunk(payload: String): String {
    val maxPayloadSize = 500
    return if (payload.length > maxPayloadSize) "${payload.take(maxPayloadSize)}..." else payload
}

fun stringifyHeaders(headers: Map<String, Collection<String>>): Map<String, String> =
    headers
        .map { it.key to it.value.joinToString(", ") }
        .toMap()
