package com.jbappz.cryptocoin.trade.ui.sell

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
import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import com.jbappz.cryptocoin.trade.domain.SellCoinUseCase
import com.jbappz.cryptocoin.util.formatCoinPrice
import cryptocoin.composeapp.generated.resources.Res
import cryptocoin.composeapp.generated.resources.error_no_internet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
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
class SellViewModelTest {

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
        val portfolioCoin = PortfolioCoinModel(
            coin = Coin(id = "1", name = "Bitcoin", symbol = "BTC", iconUrl = "url"),
            performancePercent = 0.0,
            averagePurchasePrice = 50000.0,
            ownedAmountInUnit = 0.5,
            ownedAmountInFiat = 25000.0
        )
        val fakeDataSource = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Success(
                CoinDetailsResponseDto(
                    data = CoinResponseDto(
                        coin = CoinItemDto(
                            uuid = "1",
                            symbol = "BTC",
                            name = "Bitcoin",
                            iconUrl = "url",
                            price = 60000.0,
                            rank = 1,
                            change = 0.0
                        )
                    )
                )
            )
        )
        val fakeRepository = FakePortfolioRepository(portfolioCoin)
        val getCoinDetailsUseCase = GetCoinDetailsUseCase(fakeDataSource)
        val sellCoinUseCase = SellCoinUseCase(fakeRepository)

        val viewModel = SellViewModel(getCoinDetailsUseCase, fakeRepository, sellCoinUseCase, "1")
        val state = viewModel.state.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertNotNull(state.coin)
        assertEquals("Bitcoin", state.coin.name)
        // 0.5 units * 60000 price = 30000.0 fiat value
        assertEquals("Available: ${formatCoinPrice(30000.0)}", state.availableAmount)
        assertNull(state.error)
    }

    @Test
    fun testInitialLoadPortfolioError() = runTest {
        val fakeDataSource = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Success(
                CoinDetailsResponseDto(
                    data = CoinResponseDto(
                        coin = CoinItemDto(
                            uuid = "1",
                            symbol = "BTC",
                            name = "Bitcoin",
                            iconUrl = "url",
                            price = 60000.0,
                            rank = 1,
                            change = 0.0
                        )
                    )
                )
            )
        )
        val fakeRepository = FakePortfolioRepository(portfolioCoin = null, portfolioCoinError = DataError.Remote.NO_INTERNET)
        val getCoinDetailsUseCase = GetCoinDetailsUseCase(fakeDataSource)
        val sellCoinUseCase = SellCoinUseCase(fakeRepository)

        val viewModel = SellViewModel(getCoinDetailsUseCase, fakeRepository, sellCoinUseCase, "1")
        val state = viewModel.state.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertEquals(Res.string.error_no_internet, state.error)
    }

    @Test
    fun testOnAmountChanged() = runTest {
        val portfolioCoin = PortfolioCoinModel(
            coin = Coin(id = "1", name = "Bitcoin", symbol = "BTC", iconUrl = "url"),
            performancePercent = 0.0,
            averagePurchasePrice = 50000.0,
            ownedAmountInUnit = 0.5,
            ownedAmountInFiat = 25000.0
        )
        val fakeDataSource = FakeCoinsRemoteDataSource(
            coinDetailsResult = Result.Success(
                CoinDetailsResponseDto(
                    data = CoinResponseDto(
                        coin = CoinItemDto(
                            uuid = "1",
                            symbol = "BTC",
                            name = "Bitcoin",
                            iconUrl = "url",
                            price = 60000.0,
                            rank = 1,
                            change = 0.0
                        )
                    )
                )
            )
        )
        val fakeRepository = FakePortfolioRepository(portfolioCoin)
        val getCoinDetailsUseCase = GetCoinDetailsUseCase(fakeDataSource)
        val sellCoinUseCase = SellCoinUseCase(fakeRepository)

        val viewModel = SellViewModel(getCoinDetailsUseCase, fakeRepository, sellCoinUseCase, "1")
        viewModel.state.first { !it.isLoading }

        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.state.collect {}
        }

        viewModel.onAmountChanged("250")
        val state = viewModel.state.first { it.amount == "250" }
        assertEquals("250", state.amount)
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
        private val portfolioCoin: PortfolioCoinModel?,
        private val portfolioCoinError: DataError.Remote? = null
    ) : PortfolioRepository {
        private val cashBalance = MutableStateFlow(1000.0)

        override suspend fun initialiseBalance() {}

        override fun getPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
            TODO("Not needed")
        }

        override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
            return if (portfolioCoinError != null) {
                Result.Error(portfolioCoinError)
            } else {
                Result.Success(portfolioCoin)
            }
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