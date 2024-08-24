package com.litvin.exchange.domain.usecase.fee

import com.litvin.exchange.domain.factory.FeeFactory.givenFee
import com.litvin.exchange.outbound.fake.FeeRepositoryFake
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FeeDeleteUseCaseImplTest {
    private val feeRepository = FeeRepositoryFake()
    private val sut = FeeDeleteUseCaseImpl(feeRepository)

    @BeforeEach
    fun clear() {
        feeRepository.clear()
    }

    @Test
    fun `when fee exists then remove it`() {
        val fee = givenFee(id = 1L)
        feeRepository.givenFee(fee)
        feeRepository.findAll().size shouldBe 1

        sut.deleteFee(fee.currencyFrom, fee.currencyTo)

        feeRepository.findAll().size shouldBe 0
    }

    @Test
    fun `when fee doesn't exists then ignore it`() {
        val fee = givenFee(id = 1L)
        feeRepository.givenFee(fee)
        feeRepository.findAll().size shouldBe 1

        sut.deleteFee("cur1", "cur2")

        feeRepository.findAll().size shouldBe 1
    }
}
