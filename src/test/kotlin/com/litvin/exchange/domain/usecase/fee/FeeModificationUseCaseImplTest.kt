package com.litvin.exchange.domain.usecase.fee

import com.litvin.exchange.domain.factory.FeeFactory.givenFee
import com.litvin.exchange.outbound.fake.FeeRepositoryFake
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class FeeModificationUseCaseImplTest {
    private val feeRepository = FeeRepositoryFake()
    private val sut = FeeModificationUseCaseImpl(feeRepository)

    @BeforeEach
    fun clear() {
        feeRepository.clear()
    }

    @Test
    fun `when fee does not exist then create it`() {
        feeRepository.findAll().size shouldBe 0
        val fee = givenFee()

        sut.createOrUpdateFee(fee.currencyFrom, fee.currencyTo, fee.value)

        val result = feeRepository.findAll().single()
        result.asClue {
            it.id shouldNotBe null
            it.currencyFrom shouldBe fee.currencyFrom
            it.currencyTo shouldBe fee.currencyTo
            it.value shouldBe fee.value
        }
    }

    @Test
    fun `when fee exists then update it`() {
        val fee = givenFee(id = 1L, value = BigDecimal("0.1"))
        feeRepository.givenFee(fee)

        sut.createOrUpdateFee(fee.currencyFrom, fee.currencyTo, BigDecimal("0.9"))

        val result = feeRepository.findAll().single()
        result.asClue {
            it.id shouldNotBe null
            it.currencyFrom shouldBe fee.currencyFrom
            it.currencyTo shouldBe fee.currencyTo
            it.value shouldBe BigDecimal("0.9")
        }
    }
}
