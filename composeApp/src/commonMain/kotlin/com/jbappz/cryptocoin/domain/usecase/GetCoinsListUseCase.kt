package com.jbappz.cryptocoin.domain.usecase

import com.jbappz.cryptocoin.data.mapper.toCoinModel
import com.jbappz.cryptocoin.data.mapper.toCoins
import com.jbappz.cryptocoin.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.domain.DataError
import com.jbappz.cryptocoin.domain.Result
import com.jbappz.cryptocoin.domain.map
import com.jbappz.cryptocoin.domain.model.Coin
import com.jbappz.cryptocoin.domain.model.CoinModel

class GetCoinsListUseCase(
    private val client: CoinsRemoteDataSource
) {
    suspend fun execute(): Result<List<CoinModel>, DataError.Remote> {
        return client.getListOfCoins().map { dto ->
            dto.data.coins.map {
                it.toCoinModel()
            }
        }
    }
}
