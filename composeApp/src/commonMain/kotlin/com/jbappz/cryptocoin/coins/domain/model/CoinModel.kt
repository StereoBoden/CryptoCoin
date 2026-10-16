package com.jbappz.cryptocoin.coins.domain.model

data class CoinModel(
    val coin: Coin,
    val price: Double,
    val change: Double,
)