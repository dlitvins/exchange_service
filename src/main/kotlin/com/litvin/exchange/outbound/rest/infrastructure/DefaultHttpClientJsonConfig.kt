package com.litvin.exchange.outbound.rest.infrastructure

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.codec.Decoder
import feign.codec.Encoder
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder

fun newDefaultObjectMapper(): ObjectMapper = preconfiguredJsonMapperBuilder().build()

fun preconfiguredJsonMapperBuilder(): JsonMapper.Builder {
    val jsonMapperBuilder =
        JsonMapper
            .builder()
            .enable(MapperFeature.USE_ANNOTATIONS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .addModules(
                JavaTimeModule(),
                KotlinModule.Builder().build(),
            )

    return jsonMapperBuilder
}

fun newDefaultJsonDecoder(): Decoder = JacksonDecoder(newDefaultObjectMapper())

fun newDefaultJsonEncoder(): Encoder = JacksonEncoder(newDefaultObjectMapper())
