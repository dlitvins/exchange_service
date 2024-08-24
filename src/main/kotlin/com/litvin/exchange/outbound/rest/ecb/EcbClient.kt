package com.litvin.exchange.outbound.rest.ecb

import feign.Param
import feign.RequestLine

interface EcbClient {
    @RequestLine(
        "GET /EXR/D.{currencyFrom}.EUR.SP00.A" +
            "?includeHistory=false" +
            "&lastNObservations=1" +
            "&detail=dataonly" +
            "&format=jsondata",
    )
    fun getCurrencyRates(
        @Param("currencyFrom") currencyFrom: String,
    ): EcbResponse
}
