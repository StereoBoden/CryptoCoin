package com.jbappz.cryptocoin.portfolio.data.mapper

import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioCoinEntity
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import kotlin.time.Clock

fun PortfolioCoinEntity.toPortfolioCoinModel(
    currentPrice: Double,
): PortfolioCoinModel {
   return PortfolioCoinModel(
       coin = Coin(
           id = coinId,
           name = name,
           symbol = symbol,
           iconUrl = iconUrl,
       ),
       performancePercent = ((currentPrice - averagePurchasePrice) / averagePurchasePrice) * 100,
       averagePurchasePrice = averagePurchasePrice,
       ownedAmountInUnit = amountOwned,
       ownedAmountInFiat = amountOwned * currentPrice,
   )
}

fun PortfolioCoinModel.toPortfolioCoinEntity(): PortfolioCoinEntity {
    return PortfolioCoinEntity(
        coinId = coin.id,
        name = coin.name,
        symbol = coin.symbol,
        iconUrl = coin.iconUrl,
        averagePurchasePrice = averagePurchasePrice,
        amountOwned = ownedAmountInUnit,
        timestamp = Clock.System.now().toEpochMilliseconds(),
    )
}