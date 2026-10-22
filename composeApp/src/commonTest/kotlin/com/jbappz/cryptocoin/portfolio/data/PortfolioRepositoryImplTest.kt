package com.jbappz.cryptocoin.portfolio.data

import androidx.sqlite.SQLiteException
import com.jbappz.cryptocoin.coins.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinItemDto
import com.jbappz.cryptocoin.coins.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinsListDto
import com.jbappz.cryptocoin.coins.data.dto.CoinsResponseDto
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioCoinEntity
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioDao
import com.jbappz.cryptocoin.portfolio.data.local.UserBalanceDao
import com.jbappz.cryptocoin.portfolio.data.local.UserBalanceEntity
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PortfolioRepositoryImplTest {

    private lateinit var portfolioDao: FakePortfolioDao
    private lateinit var userBalanceDao: FakeUserBalanceDao
    private lateinit var coinsRemoteDataSource: FakeCoinsRemoteDataSource
    private lateinit var repository: PortfolioRepositoryImpl

    @BeforeTest
    fun setUp() {
        portfolioDao = FakePortfolioDao()
        userBalanceDao = FakeUserBalanceDao()
        coinsRemoteDataSource = FakeCoinsRemoteDataSource()
        repository = PortfolioRepositoryImpl(
            portfolioDao = portfolioDao,
            userBalanceDao = userBalanceDao,
            coinsRemoteDataSource = coinsRemoteDataSource,
        )
    }

    @Test
    fun testInitialiseBalanceInsertsDefaultWhenBalanceIsNull() = runTest {
        userBalanceDao.balance = null

        repository.initialiseBalance()

        assertEquals(10000.0, userBalanceDao.insertedEntity?.cashBalance)
    }

    @Test
    fun testInitialiseBalanceDoesNotInsertWhenBalanceAlreadyExists() = runTest {
        userBalanceDao.balance = 5000.0

        repository.initialiseBalance()

        assertNull(userBalanceDao.insertedEntity)
        assertEquals(5000.0, userBalanceDao.getUserBalance())
    }

    @Test
    fun testGetPortfolioCoinsFlowEmitsEmptyListWhenNoCoinsOwned() = runTest {
        portfolioDao.setCoins(emptyList())

        val result = repository.getPortfolioCoinsFlow().first()

        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun testGetPortfolioCoinsFlowEmitsMappedCoinsOnSuccess() = runTest {
        val entity = PortfolioCoinEntity(
            coinId = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            iconUrl = "url",
            averagePurchasePrice = 40000.0,
            amountOwned = 1.0,
            timestamp = 1000L
        )
        portfolioDao.setCoins(listOf(entity))

        val coinDto = CoinItemDto(
            uuid = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            iconUrl = "url",
            price = 50000.0,
            rank = 1,
            change = 5.0
        )
        coinsRemoteDataSource.getListOfCoinsResult = Result.Success(
            CoinsResponseDto(CoinsListDto(listOf(coinDto)))
        )

        val result = repository.getPortfolioCoinsFlow().first()

        assertTrue(result is Result.Success)
        assertEquals(1, result.data.size)
        val model = result.data.first()
        assertEquals("btc", model.coin.id)
        assertEquals(50000.0, model.ownedAmountInFiat)
        assertEquals(25.0, model.performancePercent)
    }

    @Test
    fun testGetPortfolioCoinsFlowEmitsErrorWhenRemoteCallFails() = runTest {
        val entity = PortfolioCoinEntity(
            coinId = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            iconUrl = "url",
            averagePurchasePrice = 40000.0,
            amountOwned = 1.0,
            timestamp = 1000L
        )
        portfolioDao.setCoins(listOf(entity))
        coinsRemoteDataSource.getListOfCoinsResult = Result.Error(DataError.Remote.NO_INTERNET)

        val result = repository.getPortfolioCoinsFlow().first()

        assertTrue(result is Result.Error)
        assertEquals(DataError.Remote.NO_INTERNET, result.error)
    }

    @Test
    fun testGetPortfolioCoinReturnsSuccessWithModelWhenFound() = runTest {
        val entity = PortfolioCoinEntity(
            coinId = "eth",
            name = "Ethereum",
            symbol = "ETH",
            iconUrl = "url",
            averagePurchasePrice = 2000.0,
            amountOwned = 2.0,
            timestamp = 1000L
        )
        portfolioDao.setCoins(listOf(entity))

        val coinDto = CoinItemDto(
            uuid = "eth",
            symbol = "ETH",
            name = "Ethereum",
            iconUrl = "url",
            price = 3000.0,
            rank = 2,
            change = 2.0
        )
        coinsRemoteDataSource.getCoinByIdResult = Result.Success(
            CoinDetailsResponseDto(CoinResponseDto(coinDto))
        )

        val result = repository.getPortfolioCoin("eth")

        assertTrue(result is Result.Success)
        assertEquals("eth", result.data?.coin?.id)
        assertEquals(6000.0, result.data?.ownedAmountInFiat)
    }

    @Test
    fun testGetPortfolioCoinReturnsSuccessNullWhenNotOwnedLocally() = runTest {
        val coinDto = CoinItemDto(
            uuid = "sol",
            symbol = "SOL",
            name = "Solana",
            iconUrl = "url",
            price = 100.0,
            rank = 5,
            change = 1.0
        )
        coinsRemoteDataSource.getCoinByIdResult = Result.Success(
            CoinDetailsResponseDto(CoinResponseDto(coinDto))
        )

        val result = repository.getPortfolioCoin("sol")

        assertTrue(result is Result.Success)
        assertNull(result.data)
    }

    @Test
    fun testGetPortfolioCoinReturnsErrorWhenRemoteFails() = runTest {
        coinsRemoteDataSource.getCoinByIdResult = Result.Error(DataError.Remote.SERVER)

        val result = repository.getPortfolioCoin("btc")

        assertTrue(result is Result.Error)
        assertEquals(DataError.Remote.SERVER, result.error)
    }

    @Test
    fun testSavePortfolioCoinSuccess() = runTest {
        val model = PortfolioCoinModel(
            coin = Coin("btc", "Bitcoin", "BTC", "url"),
            performancePercent = 10.0,
            averagePurchasePrice = 40000.0,
            ownedAmountInUnit = 0.5,
            ownedAmountInFiat = 22000.0
        )

        val result = repository.savePortfolioCoin(model)

        assertTrue(result is Result.Success)
        val savedEntity = portfolioDao.getCoinById("btc")
        assertEquals("btc", savedEntity?.coinId)
        assertEquals(0.5, savedEntity?.amountOwned)
    }

    @Test
    fun testSavePortfolioCoinReturnsDiskFullOnSQLiteException() = runTest {
        portfolioDao.shouldThrowOnInsert = true
        val model = PortfolioCoinModel(
            coin = Coin("btc", "Bitcoin", "BTC", "url"),
            performancePercent = 10.0,
            averagePurchasePrice = 40000.0,
            ownedAmountInUnit = 0.5,
            ownedAmountInFiat = 22000.0
        )

        val result = repository.savePortfolioCoin(model)

        assertTrue(result is Result.Error)
        assertEquals(DataError.Local.DISK_FULL, result.error)
    }

    @Test
    fun testRemoveCoinFromPortfolioDeletesFromDao() = runTest {
        val entity = PortfolioCoinEntity(
            coinId = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            iconUrl = "url",
            averagePurchasePrice = 40000.0,
            amountOwned = 1.0,
            timestamp = 1000L
        )
        portfolioDao.setCoins(listOf(entity))

        repository.removeCoinFromPortfolio("btc")

        assertNull(portfolioDao.getCoinById("btc"))
    }

    @Test
    fun testCalculateTotalPortfolioValueZeroWhenEmpty() = runTest {
        portfolioDao.setCoins(emptyList())

        val result = repository.calculateTotalPortfolioValue().first()

        assertTrue(result is Result.Success)
        assertEquals(0.0, result.data)
    }

    @Test
    fun testCalculateTotalPortfolioValueCalculatesCorrectSum() = runTest {
        val btcEntity = PortfolioCoinEntity("btc", "Bitcoin", "BTC", "url", 40000.0, 0.5, 1000L)
        val ethEntity = PortfolioCoinEntity("eth", "Ethereum", "ETH", "url", 2000.0, 2.0, 1000L)
        portfolioDao.setCoins(listOf(btcEntity, ethEntity))

        val btcDto = CoinItemDto("btc", "BTC", "Bitcoin", "url", 50000.0, 1, 0.0)
        val ethDto = CoinItemDto("eth", "ETH", "Ethereum", "url", 3000.0, 2, 0.0)
        coinsRemoteDataSource.getListOfCoinsResult = Result.Success(
            CoinsResponseDto(CoinsListDto(listOf(btcDto, ethDto)))
        )

        val result = repository.calculateTotalPortfolioValue().first()

        assertTrue(result is Result.Success)
        assertEquals(31000.0, result.data)
    }

    @Test
    fun testTotalBalanceFlowCombinesCashAndPortfolioValue() = runTest {
        userBalanceDao.balance = 5000.0

        val btcEntity = PortfolioCoinEntity("btc", "Bitcoin", "BTC", "url", 40000.0, 1.0, 1000L)
        portfolioDao.setCoins(listOf(btcEntity))

        val btcDto = CoinItemDto("btc", "BTC", "Bitcoin", "url", 50000.0, 1, 0.0)
        coinsRemoteDataSource.getListOfCoinsResult = Result.Success(
            CoinsResponseDto(CoinsListDto(listOf(btcDto)))
        )

        val result = repository.totalBalanceFlow().first()

        assertTrue(result is Result.Success)
        assertEquals(55000.0, result.data)
    }

    @Test
    fun testTotalBalanceFlowPropagatesPortfolioError() = runTest {
        userBalanceDao.balance = 5000.0

        val btcEntity = PortfolioCoinEntity("btc", "Bitcoin", "BTC", "url", 40000.0, 1.0, 1000L)
        portfolioDao.setCoins(listOf(btcEntity))
        coinsRemoteDataSource.getListOfCoinsResult = Result.Error(DataError.Remote.TOO_MANY_REQUESTS)

        val result = repository.totalBalanceFlow().first()

        assertTrue(result is Result.Error)
        assertEquals(DataError.Remote.TOO_MANY_REQUESTS, result.error)
    }

    @Test
    fun testCashBalanceFlowDefaultsTo10000WhenNull() = runTest {
        userBalanceDao.balance = null

        val cashBalance = repository.cashBalanceFlow().first()

        assertEquals(10000.0, cashBalance)
    }

    @Test
    fun testUpdateCashBalanceUpdatesDao() = runTest {
        repository.updateCashBalance(12500.0)

        assertEquals(12500.0, userBalanceDao.getUserBalance())
    }

    private class FakePortfolioDao : PortfolioDao {
        private val coinsFlow = MutableStateFlow<List<PortfolioCoinEntity>>(emptyList())
        var shouldThrowOnInsert = false

        override suspend fun insert(portfolioCoinEntity: PortfolioCoinEntity) {
            if (shouldThrowOnInsert) {
                throw SQLiteException("Disk full")
            }
            val current = coinsFlow.value.toMutableList()
            current.removeAll { it.coinId == portfolioCoinEntity.coinId }
            current.add(portfolioCoinEntity)
            coinsFlow.value = current
        }

        override fun getAllOwnedCoins(): Flow<List<PortfolioCoinEntity>> = coinsFlow

        override suspend fun getCoinById(id: String): PortfolioCoinEntity? {
            return coinsFlow.value.find { it.coinId == id }
        }

        override suspend fun deleteCoinById(id: String) {
            val current = coinsFlow.value.toMutableList()
            current.removeAll { it.coinId == id }
            coinsFlow.value = current
        }

        fun setCoins(coins: List<PortfolioCoinEntity>) {
            coinsFlow.value = coins
        }
    }

    private class FakeUserBalanceDao : UserBalanceDao {
        var balance: Double? = null
        var updatedBalance: Double? = null
        var insertedEntity: UserBalanceEntity? = null

        override suspend fun getUserBalance(): Double? {
            return updatedBalance ?: balance
        }

        override suspend fun insertBalance(userBalanceEntity: UserBalanceEntity) {
            insertedEntity = userBalanceEntity
            balance = userBalanceEntity.cashBalance
        }

        override suspend fun updateCashBalance(newBalance: Double) {
            updatedBalance = newBalance
        }
    }

    private class FakeCoinsRemoteDataSource : CoinsRemoteDataSource {
        var getListOfCoinsResult: Result<CoinsResponseDto, DataError.Remote> = Result.Success(
            CoinsResponseDto(CoinsListDto(emptyList()))
        )
        var getCoinByIdResult: Result<CoinDetailsResponseDto, DataError.Remote> = Result.Error(DataError.Remote.UNKNOWN)

        override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
            return getListOfCoinsResult
        }

        override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
            TODO("Not required for test")
        }

        override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
            return getCoinByIdResult
        }
    }
}
