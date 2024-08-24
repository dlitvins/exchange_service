package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.exception.ExceptionCode
import com.litvin.exchange.domain.exception.UseCaseException
import com.litvin.exchange.domain.model.Fee
import com.litvin.exchange.domain.usecase.fee.FeeModificationUseCase
import com.litvin.exchange.inbound.AbstractControllerTest
import com.litvin.exchange.inbound.model.FeeDTO
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.log
import java.math.BigDecimal

class FeeModificationControllerTest : AbstractControllerTest() {
    private val feeModificationUseCase: FeeModificationUseCase = mockk()

    override val controllerURI = "/fee"
    override val controller = FeeModificationController(feeModificationUseCase)

    private val givenFeeRequest =
        FeeDTO(
            currencyFrom = "USD",
            currencyTo = "ARS",
            value = BigDecimal("0.23"),
        )
    private val predefinedFeeResponse =
        Fee(
            id = 1L,
            currencyFrom = "USD",
            currencyTo = "ARS",
            value = BigDecimal("0.23"),
        )

    @BeforeEach
    fun clear() {
        clearMocks(feeModificationUseCase)
    }

    @Nested
    @DisplayName("API contract tests")
    inner class APIContractTests {
        @Test
        fun `contract respected`() {
            every {
                feeModificationUseCase.createOrUpdateFee(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                    fee = givenFeeRequest.value,
                )
            } returns predefinedFeeResponse

            val mvcResult =
                mockMvc
                    .perform(
                        post(controllerURI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getBodyContent(givenFeeRequest)),
                    ).andDo { log() }
                    .andReturn()

            mvcResult.response.status shouldBe 200

            val result =
                objectMapper.readValue(
                    mvcResult.response.contentAsString,
                    FeeDTO::class.java,
                )

            result shouldBe givenFeeRequest
        }
    }

    @Nested
    @DisplayName("Plain Kotlin tests")
    inner class RestControllerBehaviourTests {
        @Test
        fun `when controller is called then proper use case called`() {
            every {
                feeModificationUseCase.createOrUpdateFee(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                    fee = givenFeeRequest.value,
                )
            } returns predefinedFeeResponse

            val result = controller.createOrUpdateFee(givenFeeRequest)

            verify(exactly = 1) {
                feeModificationUseCase.createOrUpdateFee(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                    fee = givenFeeRequest.value,
                )
            }

            result shouldBe givenFeeRequest
        }

        @Test
        fun `when incorrect input then throw exception with validation errors`() {
            val exception =
                shouldThrow<UseCaseException> {
                    controller.createOrUpdateFee(
                        FeeDTO(
                            currencyFrom = "",
                            currencyTo = "argentine peso",
                            value = BigDecimal("-1"),
                        ),
                    )
                }

            exception.code shouldBe ExceptionCode.INVALID_REQUEST
            exception.errorDetails shouldBe "[" +
                "ValidationError(code=VALIDATION_ERROR, errorDetails=Invalid amount." +
                "Should be between 0 and 1 and 5 significant digits after comma.), " +
                "ValidationError(code=VALIDATION_ERROR, errorDetails=Invalid from currency. Type currency in ISO format.), " +
                "ValidationError(code=VALIDATION_ERROR, errorDetails=Invalid to currency. Type currency in ISO format.)" +
                "]"
        }

        @Test
        fun `when same currencies for from and to then throw exception with validation errors`() {
            val exception =
                shouldThrow<UseCaseException> {
                    controller.createOrUpdateFee(
                        FeeDTO(
                            currencyFrom = "ARS",
                            currencyTo = "ARS",
                            value = BigDecimal("0.2"),
                        ),
                    )
                }

            exception.code shouldBe ExceptionCode.INVALID_REQUEST
            exception.errorDetails shouldBe "[ValidationError(code=VALIDATION_ERROR, errorDetails=Same from to currency)]"
        }
    }
}
