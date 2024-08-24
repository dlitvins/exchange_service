package com.litvin.exchange.outbound.db.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal

@Entity(name = "fee")
data class FeeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    val currencyFrom: String,
    val currencyTo: String,
    @Column(precision = 6, scale = 5)
    val value: BigDecimal,
)
