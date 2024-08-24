package com.litvin.exchange.outbound.repository

import com.litvin.exchange.domain.repository.EcbRepository
import com.litvin.exchange.outbound.rest.ecb.EcbClient
import com.litvin.exchange.outbound.rest.ecb.EcbResponse
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class EcbRepositoryImpl(
    private val ecbClient: EcbClient,
) : EcbRepository {
    override fun getSingleEurToCurrencyRate(currency: String): BigDecimal = mapSingleCurrencyResponse(ecbClient.getCurrencyRates(currency))

    override fun getAllEurRates(): Map<String, BigDecimal> = mapCurrencyRatesResponse(ecbClient.getCurrencyRates(""))

    private fun mapSingleCurrencyResponse(response: EcbResponse): BigDecimal =
        response.dataSets
            .first()
            .series.values
            .first()
            .observations.values
            .first()
            .first()

    private fun mapCurrencyRatesResponse(response: EcbResponse): Map<String, BigDecimal> {
        val rates = mutableMapOf<String, BigDecimal>()

        val currencies = mutableMapOf<Int, String>()
        response.structure.dimensions.series
            .firstOrNull { it.id == "CURRENCY" }
            ?.values
            ?.forEachIndexed { index, value -> currencies[index] = value.id }
            ?: throw IllegalArgumentException("CURRENCY dimension not found")

        response.dataSets
            .first()
            .series
            .onEachIndexed { index, (_, value) ->
                rates[currencies[index]!!] =
                    value.observations.values
                        .first()
                        .first()
            }

        return rates
    }
}
