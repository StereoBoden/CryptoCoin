package com.jbappz.cryptocoin.portfolio.ui

import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.EmptyResult
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import com.jbappz.cryptocoin.util.formatCoinPercentage
import com.jbappz.cryptocoin.util.formatCoinPrice
import com.jbappz.cryptocoin.util.formatCoinUnit
import cryptocoin.composeapp.generated.resources.Res
import cryptocoin.composeapp.generated.resources.error_no_internet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakePortfolioRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePortfolioRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialiseBalanceIsCalledOnFlowStart() = runTest {
        val viewModel = PortfolioViewModel(repository)

        // Wait for state flow to process upstream combine and onStart
        viewModel.state.first { !it.isLoading }

        assertTrue(repository.initialiseBalanceCalled)
    }

    @Test
    fun testSuccessStateWithCoins() = runTest {
        val btcCoin = Coin(
            id = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            iconUrl = "btc_icon_url",
        )
        val portfolioCoin = PortfolioCoinModel(
            coin = btcCoin,
            performancePercent = 12.5,
            averagePurchasePrice = 50000.0,
            ownedAmountInUnit = 0.5,
            ownedAmountInFiat = 25000.0,
        )

        repository.portfolioCoinsFlow.value = Result.Success(listOf(portfolioCoin))
        repository.totalBalanceFlow.value = Result.Success(30000.0)
        repository.cashBalanceFlow.value = 5000.0

        val viewModel = PortfolioViewModel(repository)
        val state = viewModel.state.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertTrue(state.showBuyButton)
        assertEquals(formatCoinPrice(30000.0), state.portfolioValue)
        assertEquals(formatCoinPrice(5000.0), state.cashBalance)
        assertNull(state.error)
        assertEquals(1, state.coins.size)

        val coinItem = state.coins.first()
        assertEquals("btc", coinItem.id)
        assertEquals("Bitcoin", coinItem.name)
        assertEquals("btc_icon_url", coinItem.iconUrl)
        assertEquals(formatCoinUnit(0.5, "BTC"), coinItem.amountInUnitText)
        assertEquals(formatCoinPrice(25000.0), coinItem.amountInFiatText)
        assertEquals(formatCoinPercentage(12.5), coinItem.performancePercentText)
        assertTrue(coinItem.isPositive)
    }

    @Test
    fun testSuccessStateWithEmptyCoins() = runTest {
        repository.portfolioCoinsFlow.value = Result.Success(emptyList())
        repository.totalBalanceFlow.value = Result.Success(1000.0)
        repository.cashBalanceFlow.value = 1000.0

        val viewModel = PortfolioViewModel(repository)
        val state = viewModel.state.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertFalse(state.showBuyButton)
        assertTrue(state.coins.isEmpty())
        assertEquals(formatCoinPrice(1000.0), state.portfolioValue)
        assertEquals(formatCoinPrice(1000.0), state.cashBalance)
        assertNull(state.error)
    }

    @Test
    fun testSuccessStateWhenTotalBalanceReturnsError() = runTest {
        val ethCoin = Coin(
            id = "eth",
            name = "Ethereum",
            symbol = "ETH",
            iconUrl = "eth_icon_url",
        )
        val portfolioCoin = PortfolioCoinModel(
            coin = ethCoin,
            performancePercent = -3.2,
            averagePurchasePrice = 3000.0,
            ownedAmountInUnit = 2.0,
            ownedAmountInFiat = 6000.0,
        )

        repository.portfolioCoinsFlow.value = Result.Success(listOf(portfolioCoin))
        repository.totalBalanceFlow.value = Result.Error(DataError.Remote.UNKNOWN)
        repository.cashBalanceFlow.value = 500.0

        val viewModel = PortfolioViewModel(repository)
        val state = viewModel.state.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertEquals(formatCoinPrice(0.0), state.portfolioValue)
        assertEquals(formatCoinPrice(500.0), state.cashBalance)

        val coinItem = state.coins.first()
        assertEquals(formatCoinPercentage(-3.2), coinItem.performancePercentText)
        assertFalse(coinItem.isPositive)
    }

    @Test
    fun testErrorStateWhenPortfolioCoinsReturnsError() = runTest {
        repository.portfolioCoinsFlow.value = Result.Error(DataError.Remote.NO_INTERNET)

        val viewModel = PortfolioViewModel(repository)
        val state = viewModel.state.first { !it.isLoading }

        assertFalse(state.isLoading)
        assertEquals(Res.string.error_no_internet, state.error)
        assertTrue(state.coins.isEmpty())
    }

    private class FakePortfolioRepository : PortfolioRepository {
        var initialiseBalanceCalled = false
        val portfolioCoinsFlow = MutableStateFlow<Result<List<PortfolioCoinModel>, DataError.Remote>>(
            Result.Success(emptyList()),
        )
        val totalBalanceFlow = MutableStateFlow<Result<Double, DataError.Remote>>(
            Result.Success(0.0),
        )
        val cashBalanceFlow = MutableStateFlow(0.0)

        override suspend fun initialiseBalance() {
            initialiseBalanceCalled = true
        }

        override fun getPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
            return portfolioCoinsFlow
        }

        override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
            return totalBalanceFlow
        }

        override fun cashBalanceFlow(): Flow<Double> {
            return cashBalanceFlow
        }

        override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
            TODO("Not needed for this test")
        }

        override suspend fun savePortfolioCoin(portfolioCoinModel: PortfolioCoinModel): EmptyResult<DataError.Local> {
            TODO("Not needed for this test")
        }

        override suspend fun removeCoinFromPortfolio(coinId: String) {
            TODO("Not needed for this test")
        }

        override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
            TODO("Not needed for this test")
        }

        override suspend fun updateCashBalance(newBalance: Double) {
            TODO("Not needed for this test")
        }
    }
}
