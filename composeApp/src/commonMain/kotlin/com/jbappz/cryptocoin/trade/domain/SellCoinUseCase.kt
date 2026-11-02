package com.jbappz.cryptocoin.trade.domain

import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.EmptyResult
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.firstOrNull

class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) {
    suspend fun sellCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val sellAllThreshold = 1
        when(val existingCoinResponse = portfolioRepository.getPortfolioCoin(coin.id)) {
            is Result.Success -> {
                val existingCoin = existingCoinResponse.data
                val sellAmountInUnit = amountInFiat / price
                val balance = portfolioRepository.cashBalanceFlow().firstOrNull()
                if (balance == null || existingCoin == null || (existingCoin.ownedAmountInFiat < sellAmountInUnit)) {
                    return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
                }
                val remainingInFiat = existingCoin.ownedAmountInFiat - amountInFiat
                val remainingInUnit = existingCoin.ownedAmountInUnit - sellAmountInUnit
                if (remainingInFiat < sellAllThreshold) {
                    portfolioRepository.removeCoinFromPortfolio(coin.id)
                }
                else {
                    portfolioRepository.savePortfolioCoin(
                        existingCoin.copy(
                            ownedAmountInUnit = remainingInUnit,
                            ownedAmountInFiat = remainingInFiat,
                        )
                    )
                }
                portfolioRepository.updateCashBalance(balance + amountInFiat)
                return Result.Success(Unit)
            }
            is Result.Error -> {
                return Result.Error(existingCoinResponse.error)
            }
        }
    }
}