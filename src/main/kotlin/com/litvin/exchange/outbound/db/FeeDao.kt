package com.litvin.exchange.outbound.db

import com.litvin.exchange.outbound.db.model.FeeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FeeDao : JpaRepository<FeeEntity, Long> {
    fun findFirstByCurrencyFromAndCurrencyTo(
        currencyFrom: String,
        currencyTo: String,
    ): FeeEntity?

    @Query(
        """
        SELECT f
        FROM fee f
        WHERE (:currencyFrom IS NULL OR f.currencyFrom = :currencyFrom) AND 
              (:currencyTo IS NULL OR f.currencyTo = :currencyTo) AND 
              (:cursor IS NULL OR f.id > :cursor)
        ORDER BY f.id ASC
        LIMIT :limit
    """,
    )
    fun findAllByCustomQuery(
        currencyFrom: String?,
        currencyTo: String?,
        cursor: Long?,
        limit: Int,
    ): List<FeeEntity>
}
