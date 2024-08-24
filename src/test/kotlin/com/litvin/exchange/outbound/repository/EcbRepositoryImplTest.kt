package com.litvin.exchange.outbound.repository

import com.litvin.exchange.outbound.WireMockFixture
import com.litvin.exchange.outbound.rest.ecb.config.EcbClientConfig
import com.litvin.exchange.outbound.rest.ecb.config.EcbClientFactory
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Suppress("ClassName")
class EcbRepositoryImplTest : WireMockFixture() {
    private lateinit var sut: EcbRepositoryImpl

    @BeforeAll
    fun init() {
        sut =
            EcbRepositoryImpl(
                EcbClientFactory(
                    EcbClientConfig(super.serverUrl),
                ).ecbClient(),
            )
    }

    @Test
    fun `retrieve single currency`() {
        wiremockGetStub(
            url =
                "/EXR/D.USD.EUR.SP00.A" +
                    "?includeHistory=false" +
                    "&lastNObservations=1" +
                    "&detail=dataonly" +
                    "&format=jsondata",
            responsePath = "wiremock/ecb/currency_response.json",
        )

        val rate = sut.getSingleEurToCurrencyRate("USD")

        rate shouldNotBe null
    }

    @Test
    fun `retrieve many currencies`() {
        wiremockGetStub(
            url =
                "/EXR/D..EUR.SP00.A" +
                    "?includeHistory=false" +
                    "&lastNObservations=1" +
                    "&detail=dataonly" +
                    "&format=jsondata",
            responsePath = "wiremock/ecb/currency_list_response.json",
        )

        val rates = sut.getAllEurRates()

        rates.get("ARS") shouldBe BigDecimal("91.5953")
        rates.get("USD") shouldBe BigDecimal("1.1163")
        rates.get("ZAR") shouldBe BigDecimal("19.8317")
    }
}
