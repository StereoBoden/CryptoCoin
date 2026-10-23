package com.jbappz.cryptocoin.portfolio.domain

import com.jbappz.cryptocoin.coins.domain.model.Coin

data class PortfolioCoinModel(
    val coin: Coin,
    val performancePercent: Double,
    val averagePurchasePrice: Double,
    val ownedAmountInUnit: Double,
    val ownedAmountInFiat: Double,
)