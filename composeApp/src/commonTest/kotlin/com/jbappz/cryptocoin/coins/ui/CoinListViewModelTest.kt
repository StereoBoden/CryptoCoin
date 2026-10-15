package com.jbappz.cryptocoin.coins.ui

import com.jbappz.cryptocoin.coins.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinsResponseDto
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinsListUseCase
import cryptocoin.composeapp.generated.resources.Res
import cryptocoin.composeapp.generated.resources.error_no_internet
import cryptocoin.composeapp.generated.resources.error_request_timeout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class CoinListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testViewModelErrorStateWhenUseCaseReturnsError() = runTest {
        // GIVEN
        val fakeDataSource = FakeCoinsRemoteDataSource(
            listOfCoinsResult = Result.Error(DataError.Remote.NO_INTERNET)
        )
        val useCase = GetCoinsListUseCase(fakeDataSource)
        val viewModel = CoinListViewModel(useCase)

        // WHEN
        val state = viewModel.state.first { !it.isLoading }

        // THEN
        assertFalse(state.isLoading)
        assertEquals(Res.string.error_no_internet, state.error)
        assertEquals(emptyList(), state.coins)
    }

    @Test
    fun testViewModelErrorStateWhenUseCaseReturnsTimeoutError() = runTest {
        // GIVEN
        val fakeDataSource = FakeCoinsRemoteDataSource(
            listOfCoinsResult = Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        )
        val useCase = GetCoinsListUseCase(fakeDataSource)
        val viewModel = CoinListViewModel(useCase)

        // WHEN
        val state = viewModel.state.first { !it.isLoading }

        // THEN
        assertFalse(state.isLoading)
        assertEquals(Res.string.error_request_timeout, state.error)
        assertEquals(emptyList(), state.coins)
    }

    private class FakeCoinsRemoteDataSource(
        private val listOfCoinsResult: Result<CoinsResponseDto, DataError.Remote>
    ) : CoinsRemoteDataSource {
        override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
            return listOfCoinsResult
        }

        override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
            TODO("Not needed for this test")
        }

        override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
            TODO("Not needed for this test")
        }
    }
}
