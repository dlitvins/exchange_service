package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.usecase.fee.FeeDeleteUseCase
import com.litvin.exchange.inbound.AbstractControllerTest
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.log

class FeeDeleteControllerTest : AbstractControllerTest() {
    private val feeDeleteUseCase: FeeDeleteUseCase = mockk()

    override val controllerURI = "/fee"
    override val controller = FeeDeleteController(feeDeleteUseCase)

    @BeforeEach
    fun clear() {
        clearMocks(feeDeleteUseCase)
    }

    @Nested
    @DisplayName("API contract tests")
    inner class APIContractTests {
        @Test
        fun `contract respected`() {
            every {
                feeDeleteUseCase.deleteFee(
                    currencyFrom = "USD",
                    currencyTo = "ARS",
                )
            } returns Unit

            val mvcResult =
                mockMvc
                    .perform(
                        delete(controllerURI)
                            .param("currencyFrom", "USD")
                            .param("currencyTo", "ARS"),
                    ).andDo { log() }
                    .andReturn()

            mvcResult.response.status shouldBe 200
        }
    }

    @Nested
    @DisplayName("Plain Kotlin tests")
    inner class RestControllerBehaviourTests {
        @Test
        fun `when controller is called then proper use case called`() {
            every {
                feeDeleteUseCase.deleteFee(
                    currencyFrom = "USD",
                    currencyTo = "ARS",
                )
            } returns Unit

            val result = controller.deleteFee("USD", "ARS")

            verify(exactly = 1) {
                feeDeleteUseCase.deleteFee(
                    currencyFrom = "USD",
                    currencyTo = "ARS",
                )
            }

            result shouldBe Unit
        }
    }
}
