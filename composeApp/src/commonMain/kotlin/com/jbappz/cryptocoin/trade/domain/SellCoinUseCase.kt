package com.jbappz.cryptocoin.trade.domain

import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.EmptyResult
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Use case for selling cryptocurrency coins from the portfolio.
 *
 * Validates the user's current holdings for the given coin, deducts the sold amount,
 * removes the coin from portfolio if remaining value drops below threshold,
 * and adds the sell proceeds to the cash balance.
 *
 * @property portfolioRepository The repository managing portfolio holdings and cash balance.
 */
class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) {
    /**
     * Executes a sell order for a specified coin and fiat amount.
     *
     * @param coin The coin being sold.
     * @param amountInFiat The fiat amount to sell.
     * @param price The current market price per unit of the coin.
     * @return [Result.Success] if the sale succeeds, or [Result.Error] with [DataError.Local.INSUFFICIENT_FUNDS] or repository errors.
     */
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