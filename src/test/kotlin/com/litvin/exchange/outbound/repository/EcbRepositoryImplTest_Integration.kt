package com.litvin.exchange.outbound.repository

import com.litvin.exchange.domain.repository.EcbRepository
import com.litvin.exchange.outbound.rest.ecb.config.EcbClientConfig
import com.litvin.exchange.outbound.rest.ecb.config.EcbClientFactory
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Tag("integrationTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Suppress("ClassName")
class EcbRepositoryImplTest_Integration {
    private lateinit var sut: EcbRepository

    @BeforeAll
    fun beforeAll() {
        sut =
            EcbRepositoryImpl(
                EcbClientFactory(
                    EcbClientConfig("https://data-api.ecb.europa.eu/service/data"),
                ).ecbClient(),
            )
    }

    @Test
    fun `get one currency`() {
        val rate = sut.getSingleEurToCurrencyRate("ARS")

        rate shouldNotBe null
    }

    @Test
    fun `retrieve many currencies`() {
        val rates = sut.getAllEurRates()

        rates.get("ARS") shouldNotBe null
        rates.get("USD") shouldNotBe null
        rates.get("ZAR") shouldNotBe null
    }
}
