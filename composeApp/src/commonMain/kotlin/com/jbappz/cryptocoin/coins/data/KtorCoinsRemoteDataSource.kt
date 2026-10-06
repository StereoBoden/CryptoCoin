package com.jbappz.cryptocoin.coins.data

import com.jbappz.cryptocoin.coins.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinsResponseDto
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "https://api.coinranking.com/v2"

class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient,
): CoinsRemoteDataSource {
    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> =
        safeCall {
            httpClient.get("$BASE_URL/coins")
        }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> =
        safeCall {
            httpClient.get("$BASE_URL/coin/$coinId/history")
        }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> =
        safeCall {
            httpClient.get("$BASE_URL/coin/$coinId")
        }
}
