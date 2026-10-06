package com.jbappz.cryptocoin.domain.usecase

import com.jbappz.cryptocoin.data.mapper.toCoins
import com.jbappz.cryptocoin.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.domain.DataError
import com.jbappz.cryptocoin.domain.Result
import com.jbappz.cryptocoin.domain.map
import com.jbappz.cryptocoin.domain.model.Coin

suspend fun getListOfCoinsUseCase(
    coinsRemoteDataSource: CoinsRemoteDataSource,
): Result<List<Coin>, DataError.Remote> {
    return coinsRemoteDataSource.getListOfCoins().map { it.toCoins() }
}
