package com.litvin.exchange.inbound.rest

import com.litvin.exchange.domain.usecase.CurrencyRateCacheUpdateUseCase
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.log

class CacheControllerTest : AbstractControllerTest() {
    private val currencyRateCacheUpdateUseCase: CurrencyRateCacheUpdateUseCase = mockk()

    override val controllerURI = "/cache/currency-rate"
    override val controller = CacheController(currencyRateCacheUpdateUseCase)

    @BeforeEach
    fun clear() {
        clearMocks(currencyRateCacheUpdateUseCase)
    }

    @Nested
    @DisplayName("API contract tests")
    inner class APIContractTests {
        @Test
        fun `contract respected`() {
            every {
                currencyRateCacheUpdateUseCase.resetCache()
            } returns Unit

            val mvcResult =
                mockMvc
                    .perform(
                        post("$controllerURI/reset"),
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
                currencyRateCacheUpdateUseCase.resetCache()
            } returns Unit

            val result = controller.resetCache()

            verify(exactly = 1) {
                currencyRateCacheUpdateUseCase.resetCache()
            }

            result shouldBe Unit
        }
    }
}
