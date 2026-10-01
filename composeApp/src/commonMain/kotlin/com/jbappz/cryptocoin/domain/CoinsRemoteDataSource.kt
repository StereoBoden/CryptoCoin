package com.jbappz.cryptocoin.domain

import com.jbappz.cryptocoin.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.data.dto.CoinsResponseDto

interface CoinsRemoteDataSource {

    suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote>

    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote>

    suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote>
}