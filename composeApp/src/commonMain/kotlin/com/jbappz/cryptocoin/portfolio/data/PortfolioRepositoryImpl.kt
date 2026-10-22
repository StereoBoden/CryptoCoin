package com.jbappz.cryptocoin.portfolio.data

import androidx.sqlite.SQLiteException
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.EmptyResult
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.onError
import com.jbappz.cryptocoin.coins.domain.onSuccess
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioDao
import com.jbappz.cryptocoin.portfolio.data.local.UserBalanceDao
import com.jbappz.cryptocoin.portfolio.data.local.UserBalanceEntity
import com.jbappz.cryptocoin.portfolio.data.mapper.toPortfolioCoinEntity
import com.jbappz.cryptocoin.portfolio.data.mapper.toPortfolioCoinModel
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: UserBalanceDao,
    private val coinsRemoteDataSource: CoinsRemoteDataSource,
): PortfolioRepository {
    /**
     * Initializes the user's cash balance with a default starting value if no balance entry exists.
     */
    override suspend fun initialiseBalance() {
        val currentBalance = userBalanceDao.getUserBalance()
        if (currentBalance == null) {
            userBalanceDao.insertBalance(
                UserBalanceEntity(
                    cashBalance = 10000.0,
                )
            )
        }
    }

    /**
     * Observes owned portfolio coins from local storage and enriches them with live market prices.
     *
     * @return A [Flow] emitting a [Result] containing the list of updated [PortfolioCoinModel]s or a [DataError.Remote].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> =
        portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinEntities ->
            if (portfolioCoinEntities.isEmpty()) {
                flow {
                    emit(Result.Success(emptyList()))
                }
            } else {
                flow {
                    coinsRemoteDataSource
                        .getListOfCoins()
                        .onError { emit(Result.Error(it)) }
                        .onSuccess { coinsDto ->
                            val portfolioCoins = portfolioCoinEntities.mapNotNull { portfolioCoinEntity ->
                                val coin = coinsDto.data.coins.find { it.uuid == portfolioCoinEntity.coinId }
                                coin?.let {
                                    portfolioCoinEntity.toPortfolioCoinModel(it.price)
                                }
                            }
                            emit(Result.Success(portfolioCoins))
                        }
                }
            }
            .catch {
                emit(Result.Error(DataError.Remote.UNKNOWN))
            }
        }

    /**
     * Retrieves details for a specific portfolio coin by its [coinId], combining local holdings with current remote price.
     *
     * @param coinId The unique identifier of the coin to retrieve.
     * @return A [Result] containing the [PortfolioCoinModel] if owned (or `null` if not found), or a [DataError.Remote].
     */
    override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> {
        coinsRemoteDataSource.getCoinById(coinId)
            .onError { error ->
                return Result.Error(error)
            }
            .onSuccess { coinDto ->
                val portfolioCoinEntity = portfolioDao.getCoinById(coinId)
                return if(portfolioCoinEntity != null) {
                    Result.Success(portfolioCoinEntity.toPortfolioCoinModel(coinDto.data.coin.price))
                } else {
                    Result.Success(null)
                }
            }
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    /**
     * Persists or updates a portfolio coin holding in the local database.
     *
     * @param portfolioCoinModel The [PortfolioCoinModel] representing the holding to save.
     * @return An [EmptyResult] indicating success or a [DataError.Local] on storage failure.
     */
    override suspend fun savePortfolioCoin(portfolioCoinModel: PortfolioCoinModel): EmptyResult<DataError.Local> =
        try {
            portfolioDao.insert(portfolioCoinModel.toPortfolioCoinEntity())
            return Result.Success(Unit)
        }
        catch (_: SQLiteException) {
            return Result.Error(DataError.Local.DISK_FULL)
        }

    /**
     * Removes a coin holding from the local portfolio by its [coinId].
     *
     * @param coinId The unique identifier of the coin to remove.
     */
    override suspend fun removeCoinFromPortfolio(coinId: String) {
        portfolioDao.deleteCoinById(coinId)
    }

    /**
     * Calculates the real-time total market value of all owned portfolio coins based on current remote market prices.
     *
     * @return A [Flow] emitting a [Result] containing the total portfolio value or a [DataError.Remote].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        return portfolioDao.getAllOwnedCoins().flatMapLatest { portfolioCoinEntities ->
            if (portfolioCoinEntities.isEmpty()) {
                flow {
                    emit(Result.Success(0.0))
                }
            } else {
                flow {
                    coinsRemoteDataSource
                        .getListOfCoins()
                        .onError { emit(Result.Error(it)) }
                        .onSuccess { coinsDto ->
                            val totalValue = portfolioCoinEntities.sumOf { ownedCoin ->
                                val coinPrice =
                                    coinsDto.data.coins.find { it.uuid == ownedCoin.coinId }?.price
                                        ?: 0.0
                                ownedCoin.amountOwned * coinPrice
                            }
                            emit(Result.Success(totalValue))
                        }
                }
            }
        }
        .catch {
            emit(Result.Error(DataError.Remote.UNKNOWN))
        }
    }

    /**
     * Observes the user's total net balance, combining cash balance and current portfolio valuation.
     *
     * @return A [Flow] emitting a [Result] containing total balance or a [DataError.Remote].
     */
    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> = combine(
        cashBalanceFlow(),
        calculateTotalPortfolioValue()
    ) { cashBalance, portfolioValue ->
        when (portfolioValue) {
            is Result.Success -> {
                Result.Success(cashBalance + portfolioValue.data)
            }
            is Result.Error -> {
                Result.Error(portfolioValue.error)
            }
        }
    }

    /**
     * Observes the user's uninvested cash balance.
     *
     * @return A [Flow] emitting the current cash balance amount.
     */
    override fun cashBalanceFlow(): Flow<Double> = flow {
        emit(userBalanceDao.getUserBalance() ?: 10000.0)
    }

    /**
     * Updates the user's uninvested cash balance in local storage.
     *
     * @param newBalance The updated cash balance amount to save.
     */
    override suspend fun updateCashBalance(newBalance: Double) {
        userBalanceDao.updateCashBalance(newBalance)
    }
}