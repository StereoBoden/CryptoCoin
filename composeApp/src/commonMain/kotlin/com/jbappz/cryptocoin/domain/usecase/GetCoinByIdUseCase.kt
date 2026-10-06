package com.jbappz.cryptocoin.domain.usecase

import com.jbappz.cryptocoin.data.mapper.toCoin
import com.jbappz.cryptocoin.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.domain.DataError
import com.jbappz.cryptocoin.domain.Result
import com.jbappz.cryptocoin.domain.map
import com.jbappz.cryptocoin.domain.model.Coin

suspend fun getCoinByIdUseCase(
    coinsRemoteDataSource: CoinsRemoteDataSource,
    coinId: String,
): Result<Coin, DataError.Remote> {
    return coinsRemoteDataSource.getCoinById(coinId).map { it.toCoin() }
}
