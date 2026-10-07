package com.jbappz.cryptocoin.coins.domain.usecase

import com.jbappz.cryptocoin.coins.data.mapper.toCoinPrices
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.map
import com.jbappz.cryptocoin.coins.domain.model.CoinPrice

class GetCoinPriceHistoryUseCase(
    private val client: CoinsRemoteDataSource,
) {
    suspend fun execute(coinId: String): Result<List<CoinPrice>, DataError.Remote> {
        return client.getPriceHistory(coinId).map { it.toCoinPrices() }
    }
}