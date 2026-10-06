package com.jbappz.cryptocoin.coins.domain.usecase

import com.jbappz.cryptocoin.coins.data.mapper.toCoinPrices
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.map
import com.jbappz.cryptocoin.coins.domain.model.CoinPrice

suspend fun getPriceHistoryUseCase(
    coinsRemoteDataSource: com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource,
    coinId: String,
): com.jbappz.cryptocoin.coins.domain.Result<List<com.jbappz.cryptocoin.coins.domain.model.CoinPrice>, com.jbappz.cryptocoin.coins.domain.DataError.Remote> {
    return coinsRemoteDataSource.getPriceHistory(coinId).map { it.toCoinPrices() }
}
