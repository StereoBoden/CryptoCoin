package com.jbappz.cryptocoin.trade.domain

import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.EmptyResult
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.firstOrNull

class BuyCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) {
    suspend fun buyCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val balance = portfolioRepository.cashBalanceFlow().firstOrNull()
        if (balance == null || (balance < amountInFiat)) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        val existingCoinResult = portfolioRepository.getPortfolioCoin(coin.id)
        val existingCoin = when (existingCoinResult) {
            is Result.Success -> existingCoinResult.data
            is Result.Error -> return Result.Error(existingCoinResult.error)
        }
        val amountInUnit = amountInFiat / price
        if (existingCoin != null) {
            val newAmountInUnit = existingCoin.ownedAmountInUnit + amountInUnit
            val newTotalInvestment = existingCoin.ownedAmountInFiat + amountInFiat
            val averagePurchasePrice = newTotalInvestment / newAmountInUnit
            portfolioRepository.savePortfolioCoin(
                existingCoin.copy(
                    ownedAmountInUnit = newAmountInUnit,
                    ownedAmountInFiat = newTotalInvestment,
                    averagePurchasePrice = averagePurchasePrice,
                )
            )
        } else {
            portfolioRepository.savePortfolioCoin(
                PortfolioCoinModel(
                    coin = coin,
                    performancePercent = 0.0,
                    averagePurchasePrice = price,
                    ownedAmountInFiat = amountInFiat,
                    ownedAmountInUnit = amountInUnit,
                )
            )
        }
        portfolioRepository.updateCashBalance(balance - amountInFiat)
        return Result.Success(Unit)
    }
}