package com.jbappz.cryptocoin.coins.domain.usecase

import com.jbappz.cryptocoin.coins.data.mapper.toCoinModel
import com.jbappz.cryptocoin.coins.data.mapper.toCoins
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.map
import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.coins.domain.model.CoinModel

class GetCoinsListUseCase(
    private val client: com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
) {
    suspend fun execute(): com.jbappz.cryptocoin.coins.domain.Result<List<com.jbappz.cryptocoin.coins.domain.model.CoinModel>, com.jbappz.cryptocoin.coins.domain.DataError.Remote> {
        return client.getListOfCoins().map { dto ->
            dto.data.coins.map {
                it.toCoinModel()
            }
        }
    }
}
