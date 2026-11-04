package com.jbappz.cryptocoin.trade.ui.buy

import com.jbappz.cryptocoin.coins.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinItemDto
import com.jbappz.cryptocoin.coins.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinsResponseDto
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.EmptyResult
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinDetailsUseCase
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import com.jbappz.cryptocoin.trade.domain.BuyCoinUseCase
import com.jbappz.cryptocoin.trade.ui.TradeEvent
import com.jbappz.cryptocoin.trade.ui.common.TradeAction
import com.jbappz.cryptocoin.util.formatCoinPrice
import cryptocoin.composeapp.generated.resources.Res
import cryptocoin.composeapp.generated.resources.error_insufficient_balance
import cryptocoin.composeapp.generated.resources.error_no_internet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class BuyViewModelTest {

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
    fun testInitialLoadSuccess() = runTest {
        val fakeDataSource = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Success(
                CoinDetailsResponseDto(
                    data = CoinResponseDto(
                        coin = CoinItemDto(
                            uuid = "1",
                            symbol = "BTC",
                            name = "Bitcoin",
                            iconUrl = "url",
                            price = 50000.0,
                            rank = 1,
                            change = 0.0
                        )
                    )
                )
            )
        )
        val fakeRepository = FakePortfolioRepository(initialCashBalance = 10000.0)
        val getCoinDetailsUseCase = GetCoinDetailsUseCase(fakeDataSource)
        val buyCoinUseCase = BuyCoinUseCase(fakeRepository)

        val viewModel = BuyViewModel(getCoinDetailsUseCase, fakeRepository, buyCoinUseCase, "1")
        val state = viewModel.state.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertNotNull(state.coin)
        assertEquals("Bitcoin", state.coin.name)
        assertEquals("Available: ${formatCoinPrice(10000.0)}", state.availableAmount)
        assertNull(state.error)
    }

    @Test
    fun testInitialLoadError() = runTest {
        val fakeDataSource = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Error(DataError.Remote.NO_INTERNET)
        )
        val fakeRepository = FakePortfolioRepository(initialCashBalance = 10000.0)
        val getCoinDetailsUseCase = GetCoinDetailsUseCase(fakeDataSource)
        val buyCoinUseCase = BuyCoinUseCase(fakeRepository)

        val viewModel = BuyViewModel(getCoinDetailsUseCase, fakeRepository, buyCoinUseCase, "1")
        val state = viewModel.state.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertEquals(Res.string.error_no_internet, state.error)
    }

    @Test
    fun testOnAmountChanged() = runTest {
        val fakeDataSource = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Success(
                CoinDetailsResponseDto(
                    data = CoinResponseDto(
                        coin = CoinItemDto(
                            uuid = "1",
                            symbol = "BTC",
                            name = "Bitcoin",
                            iconUrl = "url",
                            price = 50000.0,
                            rank = 1,
                            change = 0.0
                        )
                    )
                )
            )
        )
        val fakeRepository = FakePortfolioRepository(initialCashBalance = 10000.0)
        val getCoinDetailsUseCase = GetCoinDetailsUseCase(fakeDataSource)
        val buyCoinUseCase = BuyCoinUseCase(fakeRepository)

        val viewModel = BuyViewModel(getCoinDetailsUseCase, fakeRepository, buyCoinUseCase, "1")
        viewModel.state.first { !it.isLoading }

        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onAction(TradeAction.OnAmountChanged("500"))
        val state = viewModel.state.first { it.amount == "500" }
        assertEquals("500", state.amount)
        collectJob.cancel()
    }

    @Test
    fun testOnActionBuySuccessEmitsNavigateEvent() = runTest {
        val fakeDataSource = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Success(
                CoinDetailsResponseDto(
                    data = CoinResponseDto(
                        coin = CoinItemDto(
                            uuid = "1",
                            symbol = "BTC",
                            name = "Bitcoin",
                            iconUrl = "url",
                            price = 50000.0,
                            rank = 1,
                            change = 0.0
                        )
                    )
                )
            )
        )
        val fakeRepository = FakePortfolioRepository(initialCashBalance = 10000.0)
        val getCoinDetailsUseCase = GetCoinDetailsUseCase(fakeDataSource)
        val buyCoinUseCase = BuyCoinUseCase(fakeRepository)

        val viewModel = BuyViewModel(getCoinDetailsUseCase, fakeRepository, buyCoinUseCase, "1")
        viewModel.state.first { !it.isLoading }

        val eventsList = mutableListOf<TradeEvent>()
        val eventsJob = backgroundScope.launch(testDispatcher) {
            viewModel.events.collect { eventsList.add(it) }
        }

        viewModel.onAction(TradeAction.OnAmountChanged("500"))
        viewModel.onAction(TradeAction.OnSubmitClicked)
        advanceUntilIdle()

        assertEquals(TradeEvent.NavigateToPortfolio, eventsList.firstOrNull())
        eventsJob.cancel()
    }

    @Test
    fun testOnActionBuyError() = runTest {
        val fakeDataSource = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Success(
                CoinDetailsResponseDto(
                    data = CoinResponseDto(
                        coin = CoinItemDto(
                            uuid = "1",
                            symbol = "BTC",
                            name = "Bitcoin",
                            iconUrl = "url",
                            price = 50000.0,
                            rank = 1,
                            change = 0.0
                        )
                    )
                )
            )
        )
        // Cash balance of 10.0 is less than purchase amount 500.0
        val fakeRepository = FakePortfolioRepository(initialCashBalance = 10.0)
        val getCoinDetailsUseCase = GetCoinDetailsUseCase(fakeDataSource)
        val buyCoinUseCase = BuyCoinUseCase(fakeRepository)

        val viewModel = BuyViewModel(getCoinDetailsUseCase, fakeRepository, buyCoinUseCase, "1")
        viewModel.state.first { !it.isLoading }

        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onAction(TradeAction.OnAmountChanged("500"))
        viewModel.onAction(TradeAction.OnSubmitClicked)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.error)
        assertEquals(Res.string.error_insufficient_balance, state.error)
        collectJob.cancel()
    }

    private class FakeCoinsRemoteDataSource(
        private val coinDetailsResult: Result<CoinDetailsResponseDto, DataError.Remote>
    ) : CoinsRemoteDataSource {
        override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
            TODO("Not needed")
        }

        override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
            TODO("Not needed")
        }

        override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
            return coinDetailsResult
        }
    }

    private class FakePortfolioRepository(
        initialCashBalance: Double
    ) : PortfolioRepository {
        private val cashBalance = MutableStateFlow(initialCashBalance)

        override suspend fun initialiseBalance() {}

        override fun getPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
            TODO("Not needed")
        }

        override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
            return Result.Success(null)
        }

        override suspend fun savePortfolioCoin(portfolioCoinModel: PortfolioCoinModel): EmptyResult<DataError.Local> {
            return Result.Success(Unit)
        }

        override suspend fun removeCoinFromPortfolio(coinId: String) {}

        override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
            TODO("Not needed")
        }

        override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
            TODO("Not needed")
        }

        override fun cashBalanceFlow(): Flow<Double> {
            return cashBalance
        }

        override suspend fun updateCashBalance(newBalance: Double) {
            cashBalance.value = newBalance
        }
    }
}