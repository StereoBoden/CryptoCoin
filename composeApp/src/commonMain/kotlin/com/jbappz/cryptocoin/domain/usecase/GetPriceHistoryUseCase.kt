package com.jbappz.cryptocoin.domain.usecase

import com.jbappz.cryptocoin.data.mapper.toCoinPrices
import com.jbappz.cryptocoin.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.domain.DataError
import com.jbappz.cryptocoin.domain.Result
import com.jbappz.cryptocoin.domain.map
import com.jbappz.cryptocoin.domain.model.CoinPrice

suspend fun getPriceHistoryUseCase(
    coinsRemoteDataSource: CoinsRemoteDataSource,
    coinId: String,
): Result<List<CoinPrice>, DataError.Remote> {
    return coinsRemoteDataSource.getPriceHistory(coinId).map { it.toCoinPrices() }
}
