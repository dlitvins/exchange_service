package com.litvin.exchange.outbound.rest.ecb

import java.math.BigDecimal

data class EcbResponse(
    val dataSets: List<DataSet>,
    val structure: Structure,
)

data class DataSet(
    val series: Map<String, Series>,
)

data class Series(
    val observations: Map<String, List<BigDecimal>>,
)

data class Structure(
    val dimensions: Dimensions,
)

data class Dimensions(
    val series: List<Dimension>,
)

data class Dimension(
    val id: String,
    val name: String,
    val values: List<DimensionValue>,
)

data class DimensionValue(
    val id: String,
    val name: String,
)
