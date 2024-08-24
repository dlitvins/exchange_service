package com.litvin.exchange.outbound.repository

import com.litvin.exchange.domain.model.Fee
import com.litvin.exchange.outbound.PostgreSQLFixture
import com.litvin.exchange.outbound.db.FeeDao
import io.kotest.assertions.asClue
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeeRepositoryImplTest : PostgreSQLFixture() {
    private lateinit var sut: FeeRepositoryImpl
    private lateinit var feeDao: FeeDao

    @BeforeEach
    fun init() {
        feeDao = repositoryFactory.getRepository(FeeDao::class.java)
        sut = FeeRepositoryImpl(feeDao)
    }

    @Test
    fun `create fee`() {
        val fee =
            Fee(
                id = null,
                currencyFrom = "ARS",
                currencyTo = "USD",
                value = BigDecimal("0.1"),
            )
        withTransaction {
            sut.createOrUpdateFee(fee)
        }

        val entities = feeDao.findAll()
        entities.single().asClue {
            it.id shouldNotBe null
            it.currencyFrom shouldBe fee.currencyFrom
            it.currencyTo shouldBe fee.currencyTo
            it.value shouldBe fee.value
        }
    }

    @Test
    fun `update fee`() {
        val fee =
            Fee(
                id = null,
                currencyFrom = "ARS",
                currencyTo = "USD",
                value = BigDecimal("0.1"),
            )
        val result =
            withTransaction {
                sut.createOrUpdateFee(fee)
            }

        withTransaction {
            sut.createOrUpdateFee(result.copy(value = BigDecimal("0.2")))
        }

        val entities = feeDao.findAll()
        entities.single().asClue {
            it.id shouldNotBe null
            it.currencyFrom shouldBe fee.currencyFrom
            it.currencyTo shouldBe fee.currencyTo
            it.value shouldBe BigDecimal("0.2")
        }
    }

    @Test
    fun `delete fee`() {
        val fee =
            Fee(
                id = null,
                currencyFrom = "ARS",
                currencyTo = "USD",
                value = BigDecimal("0.1"),
            )
        val result =
            withTransaction {
                sut.createOrUpdateFee(fee)
            }

        withTransaction {
            sut.deleteFee(result.id!!)
        }

        val entities = feeDao.findAll()
        entities.size shouldBe 0
    }

    @Test
    fun `find fee by currencies`() {
        val fee =
            Fee(
                id = null,
                currencyFrom = "ARS",
                currencyTo = "USD",
                value = BigDecimal("0.1"),
            )
        withTransaction {
            sut.createOrUpdateFee(fee)
        }

        val result =
            sut.getFeeByCurrencies(
                currencyFrom = "ARS",
                currencyTo = "USD",
            )

        result?.asClue {
            it.id shouldNotBe null
            it.currencyFrom shouldBe fee.currencyFrom
            it.currencyTo shouldBe fee.currencyTo
            it.value shouldBe fee.value
        } ?: fail("fee not found")
    }

    @Test
    fun `when find fees by currencies then list fees by currencies`() {
        val template =
            Fee(
                id = null,
                currencyFrom = "x",
                currencyTo = "USD",
                value = BigDecimal("0.1"),
            )
        withTransaction {
            for (i in 1..10) {
                sut.createOrUpdateFee(
                    template.copy(
                        currencyFrom = "ID$i",
                        value = BigDecimal("$i").divide(BigDecimal("100"), 3, RoundingMode.HALF_EVEN),
                    ),
                )
            }
        }

        val result = sut.getAllFees(currencyFrom = "ID1", currencyTo = "USD", cursor = null, limit = 10)

        result.size shouldBe 1
        result.single().currencyFrom shouldBe "ID1"
    }

    @Test
    fun `when find fees by currencies cursor is null then return first page`() {
        val template =
            Fee(
                id = null,
                currencyFrom = "x",
                currencyTo = "y",
                value = BigDecimal("0.1"),
            )
        withTransaction {
            for (i in 1..10) {
                sut.createOrUpdateFee(
                    template.copy(
                        id = i.toLong(),
                        currencyTo = "ID$i",
                        value = BigDecimal("$i").divide(BigDecimal("100"), 3, RoundingMode.HALF_EVEN),
                    ),
                )
            }
        }

        val result = sut.getAllFees(currencyFrom = null, currencyTo = null, cursor = null, limit = 3)

        result.size shouldBe 3
        result.map { it.id } shouldContainInOrder listOf(1, 2, 3)
    }

    @Test
    fun `when find fees by currencies cursor is not null then return next items`() {
        val template =
            Fee(
                id = null,
                currencyFrom = "x",
                currencyTo = "y",
                value = BigDecimal("0.1"),
            )
        withTransaction {
            for (i in 1..10) {
                sut.createOrUpdateFee(
                    template.copy(
                        id = i.toLong(),
                        currencyTo = "ID$i",
                        value = BigDecimal("$i").divide(BigDecimal("100"), 3, RoundingMode.HALF_EVEN),
                    ),
                )
            }
        }

        val result = sut.getAllFees(currencyFrom = null, currencyTo = null, cursor = 8, limit = 3)

        result.size shouldBe 2
        result.map { it.id } shouldContainInOrder listOf(9, 10)
    }
}
