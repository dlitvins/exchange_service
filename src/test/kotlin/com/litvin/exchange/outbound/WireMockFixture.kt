package com.litvin.exchange.outbound

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.litvin.exchange.FileUtils.getTextFromResource
import com.litvin.exchange.outbound.rest.infrastructure.newDefaultObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.net.URLEncoder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class WireMockFixture {
    protected lateinit var serverUrl: String
    protected lateinit var server: WireMockServer
    protected lateinit var objectMapper: ObjectMapper

    private var serverPort: Int = 63123

    @BeforeAll
    fun baseBeforeAll() {
        serverUrl = "http://localhost:$serverPort"
        server = WireMockServer(WireMockConfiguration.options().port(serverPort))
        server.start()

        objectMapper = newDefaultObjectMapper()
    }

    @AfterEach
    fun baseAfterEach() {
        server.resetAll()
    }

    @AfterAll
    fun baseAfterAll() {
        server.stop()
    }

    protected fun wiremockGetStub(
        url: String,
        responsePath: String,
    ) {
        server.stubFor(
            get(urlEqualTo(url))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBody(getTextFromResource(responsePath)),
                ),
        )
    }

    protected fun toJsonString(obj: Any): String = objectMapper.writeValueAsString(obj)

    protected fun String.enc(): String = URLEncoder.encode(this, "UTF-8")
}
