package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.exception.ExceptionCode
import com.litvin.exchange.domain.exception.UseCaseException
import com.litvin.exchange.domain.model.Fee
import com.litvin.exchange.domain.usecase.fee.FeeReadUseCase
import com.litvin.exchange.inbound.AbstractControllerTest
import com.litvin.exchange.inbound.model.FeeDTO
import com.litvin.exchange.inbound.model.FeePageResponse
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
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.log
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeeReadControllerTest : AbstractControllerTest() {
    private val feeReadUseCase: FeeReadUseCase = mockk()

    override val controllerURI = "/fee"
    override val controller = FeeReadController(feeReadUseCase)

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
        clearMocks(feeReadUseCase)
    }

    @Nested
    @DisplayName("API contract tests")
    inner class APIContractTests {
        @Test
        fun `get currency contract respected`() {
            every {
                feeReadUseCase.getFee(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                )
            } returns predefinedFeeResponse

            val mvcResult =
                mockMvc
                    .perform(
                        get(controllerURI)
                            .param("currencyFrom", givenFeeRequest.currencyFrom)
                            .param("currencyTo", givenFeeRequest.currencyTo),
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

        @Test
        fun `get currency list contract respected`() {
            every {
                feeReadUseCase.getFees(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                    cursor = 1L,
                    limit = 50,
                )
            } returns listOf(predefinedFeeResponse)

            val mvcResult =
                mockMvc
                    .perform(
                        get("$controllerURI/list")
                            .param("currencyFrom", givenFeeRequest.currencyFrom)
                            .param("currencyTo", givenFeeRequest.currencyTo)
                            .param("cursor", "1")
                            .param("limit", "50"),
                    ).andDo { log() }
                    .andReturn()

            mvcResult.response.status shouldBe 200

            val result =
                objectMapper.readValue(
                    mvcResult.response.contentAsString,
                    FeePageResponse::class.java,
                )

            result shouldBe
                FeePageResponse(
                    items = listOf(givenFeeRequest),
                    nextPageToken = 1L,
                )
        }
    }

    @Nested
    @DisplayName("Plain Kotlin tests")
    inner class RestControllerBehaviourTests {
        @Test
        fun `when get currency is called then proper use case called`() {
            every {
                feeReadUseCase.getFee(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                )
            } returns predefinedFeeResponse

            val result = controller.getFee(givenFeeRequest.currencyFrom, givenFeeRequest.currencyTo)

            verify(exactly = 1) {
                feeReadUseCase.getFee(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                )
            }

            result shouldBe givenFeeRequest
        }

        @Test
        fun `when get currency list is called then proper use case called`() {
            every {
                feeReadUseCase.getFees(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                    cursor = 1L,
                    limit = 50,
                )
            } returns listOf(predefinedFeeResponse)

            val result =
                controller.getFees(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                    cursor = 1L,
                    limit = 50,
                )

            verify(exactly = 1) {
                feeReadUseCase.getFees(
                    currencyFrom = givenFeeRequest.currencyFrom,
                    currencyTo = givenFeeRequest.currencyTo,
                    cursor = 1L,
                    limit = 50,
                )
            }

            result shouldBe
                FeePageResponse(
                    items = listOf(givenFeeRequest),
                    nextPageToken = 1L,
                )
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "-1, Invalid limit - min value is 1",
                "1001, Invalid limit - max value is 1000",
            ],
        )
        fun `when incorrect limit then throw exception with validation errors`(
            limit: Int,
            error: String,
        ) {
            val exception =
                shouldThrow<UseCaseException> {
                    controller.getFees(
                        currencyFrom = null,
                        currencyTo = null,
                        limit = limit,
                    )
                }

            exception.code shouldBe ExceptionCode.INVALID_REQUEST
            exception.errorDetails shouldBe "[ValidationError(code=VALIDATION_ERROR, errorDetails=$error)]"
        }
    }
}
