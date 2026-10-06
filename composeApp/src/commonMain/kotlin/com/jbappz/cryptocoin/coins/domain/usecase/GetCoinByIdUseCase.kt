package com.jbappz.cryptocoin.coins.domain.usecase

import com.jbappz.cryptocoin.coins.data.mapper.toCoin
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.map
import com.jbappz.cryptocoin.coins.domain.model.Coin

suspend fun getCoinByIdUseCase(
    coinsRemoteDataSource: com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource,
    coinId: String,
): com.jbappz.cryptocoin.coins.domain.Result<com.jbappz.cryptocoin.coins.domain.model.Coin, com.jbappz.cryptocoin.coins.domain.DataError.Remote> {
    return coinsRemoteDataSource.getCoinById(coinId).map { it.toCoin() }
}
