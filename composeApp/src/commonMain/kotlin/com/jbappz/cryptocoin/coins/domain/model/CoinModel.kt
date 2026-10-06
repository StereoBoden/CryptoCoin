package com.jbappz.cryptocoin.coins.domain.model

data class CoinModel(
    val coin: com.jbappz.cryptocoin.coins.domain.model.Coin,
    val coinPrice: com.jbappz.cryptocoin.coins.domain.model.CoinPrice,
)