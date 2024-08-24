package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.exception.ExceptionCode
import com.litvin.exchange.domain.exception.UseCaseException
import com.litvin.exchange.domain.usecase.ExchangeCalculatorUseCase
import com.litvin.exchange.inbound.AbstractControllerTest
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

class ExchangeControllerTest : AbstractControllerTest() {
    private val exchangeCalculatorUseCase: ExchangeCalculatorUseCase = mockk()

    override val controllerURI = "/exchange"
    override val controller = ExchangeController(exchangeCalculatorUseCase)

    private val givenExchangeRequest =
        ExchangeController.ExchangeRequest(
            amount = BigDecimal("216"),
            currencyFrom = "USD",
            currencyTo = "ARS",
        )
    private val predefinedExchangeResponse = BigDecimal("123")

    @BeforeEach
    fun clear() {
        clearMocks(exchangeCalculatorUseCase)
    }

    @Nested
    @DisplayName("API contract tests")
    inner class APIContractTests {
        @Test
        fun `contract respected`() {
            every {
                exchangeCalculatorUseCase.calculateConversion(
                    amount = givenExchangeRequest.amount,
                    currencyFrom = givenExchangeRequest.currencyFrom,
                    currencyTo = givenExchangeRequest.currencyTo,
                )
            } returns predefinedExchangeResponse

            val mvcResult =
                mockMvc
                    .perform(
                        post(controllerURI)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(getBodyContent(givenExchangeRequest)),
                    ).andDo { log() }
                    .andReturn()

            mvcResult.response.status shouldBe 200

            val result =
                objectMapper.readValue(
                    mvcResult.response.contentAsString,
                    ExchangeController.ExchangeResponse::class.java,
                )

            result.resultAmount shouldBe predefinedExchangeResponse
        }
    }

    @Nested
    @DisplayName("Plain Kotlin tests")
    inner class RestControllerBehaviourTests {
        @Test
        fun `when controller is called then proper use case called`() {
            every {
                exchangeCalculatorUseCase.calculateConversion(
                    amount = givenExchangeRequest.amount,
                    currencyFrom = givenExchangeRequest.currencyFrom,
                    currencyTo = givenExchangeRequest.currencyTo,
                )
            } returns predefinedExchangeResponse

            val result = controller.calculateExchange(givenExchangeRequest)

            verify(exactly = 1) {
                exchangeCalculatorUseCase.calculateConversion(
                    amount = givenExchangeRequest.amount,
                    currencyFrom = givenExchangeRequest.currencyFrom,
                    currencyTo = givenExchangeRequest.currencyTo,
                )
            }

            result.resultAmount shouldBe predefinedExchangeResponse
        }

        @Test
        fun `when incorrect input then throw exception with validation errors`() {
            val exception =
                shouldThrow<UseCaseException> {
                    controller.calculateExchange(
                        ExchangeController.ExchangeRequest(
                            amount = BigDecimal("-1"),
                            currencyFrom = "   ",
                            currencyTo = "",
                        ),
                    )
                }

            exception.code shouldBe ExceptionCode.INVALID_REQUEST
            exception.errorDetails shouldBe "[" +
                "ValidationError(code=VALIDATION_ERROR, errorDetails=Invalid amount), " +
                "ValidationError(code=VALIDATION_ERROR, errorDetails=Invalid from currency), " +
                "ValidationError(code=VALIDATION_ERROR, errorDetails=Invalid to currency)" +
                "]"
        }
    }
}
