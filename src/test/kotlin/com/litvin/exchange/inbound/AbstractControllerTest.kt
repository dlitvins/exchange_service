package com.litvin.exchange.inbound

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.litvin.exchange.inbound.configuration.RestExceptionHandler
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.TimeZone

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractControllerTest {
    protected lateinit var mockMvc: MockMvc
    protected lateinit var objectMapper: ObjectMapper

    abstract val controllerURI: String
    abstract val controller: Any

    @BeforeAll
    fun setUp() {
        val converter = jacksonHttpMessageConverter()
        objectMapper = converter.objectMapper

        mockMvc =
            MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(converter)
                .setControllerAdvice(RestExceptionHandler())
                .build()
    }

    open fun jacksonHttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val converter = MappingJackson2HttpMessageConverter()
        converter.objectMapper.apply {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            setTimeZone(TimeZone.getTimeZone("UTC"))
        }
        return converter
    }

    fun getBodyContent(obj: Any): String = objectMapper.writeValueAsString(obj)
}
