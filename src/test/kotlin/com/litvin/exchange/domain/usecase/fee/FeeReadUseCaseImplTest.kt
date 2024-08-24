package com.litvin.exchange.domain.usecase.fee

import com.litvin.exchange.domain.configuration.StandardFee
import com.litvin.exchange.domain.factory.FeeFactory.givenFee
import com.litvin.exchange.outbound.fake.FeeRepositoryFake
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode

class FeeReadUseCaseImplTest {
    private val defaultFee = BigDecimal("0.5")
    private val feeRepository = FeeRepositoryFake()
    private val standardFee = StandardFee(defaultFee)
    private val sut = FeeReadUseCaseImpl(standardFee, feeRepository)

    @BeforeEach
    fun clear() {
        feeRepository.clear()
    }

    @Test
    fun `when currency pair found return it`() {
        val fee =
            givenFee(
                id = 1L,
                currencyTo = "ARS",
                currencyFrom = "USD",
                value = BigDecimal("0.3"),
            )
        feeRepository.givenFee(fee)

        val result = sut.getFee(fee.currencyFrom, fee.currencyTo)

        result.value shouldBe BigDecimal("0.3")
    }

    @Test
    fun `when currency pair not found return standard fee`() {
        val result = sut.getFee("ARS", "USD")

        result.value shouldBe defaultFee
    }

    @Test
    fun `when currency passed then filter list`() {
        val fee =
            givenFee(
                id = 1L,
                currencyTo = "ARS",
                currencyFrom = "USD",
                value = BigDecimal("0.3"),
            )
        feeRepository.givenFee(fee)

        val result = sut.getFees("ARS", "USD", null, 5)

        result.single().asClue {
            it.id shouldBe fee.id
            it.currencyFrom shouldBe fee.currencyFrom
            it.currencyTo shouldBe fee.currencyTo
            it.value shouldBe fee.value
        }
    }

    @Test
    fun `when cursor is null return first page`() {
        val template = givenFee()
        for (i in 1..10) {
            feeRepository.givenFee(
                template.copy(
                    id = i.toLong(),
                    currencyTo = "ID$i",
                    value = BigDecimal("$i").divide(BigDecimal("100"), 3, RoundingMode.HALF_EVEN),
                ),
            )
        }

        val result = sut.getFees(currencyFrom = null, currencyTo = null, cursor = null, limit = 3)

        result.size shouldBe 3
        result.map { it.id } shouldContainInOrder listOf(1, 2, 3)
    }

    @Test
    fun `when cursor is not null return next items`() {
        val template = givenFee()
        for (i in 1..10) {
            feeRepository.givenFee(
                template.copy(
                    id = i.toLong(),
                    currencyTo = "ID$i",
                    value = BigDecimal("$i").divide(BigDecimal("100"), 3, RoundingMode.HALF_EVEN),
                ),
            )
        }

        val result = sut.getFees(currencyFrom = null, currencyTo = null, cursor = 8L, limit = 3)

        result.size shouldBe 2
        result.map { it.id } shouldContainInOrder listOf(9, 10)
    }
}
