package com.jbappz.cryptocoin.coins.domain

import com.jbappz.cryptocoin.coins.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinsResponseDto

interface CoinsRemoteDataSource {

    suspend fun getListOfCoins(): com.jbappz.cryptocoin.coins.domain.Result<com.jbappz.cryptocoin.coins.data.dto.CoinsResponseDto, com.jbappz.cryptocoin.coins.domain.DataError.Remote>

    suspend fun getPriceHistory(coinId: String): com.jbappz.cryptocoin.coins.domain.Result<com.jbappz.cryptocoin.coins.data.dto.CoinPriceHistoryResponseDto, com.jbappz.cryptocoin.coins.domain.DataError.Remote>

    suspend fun getCoinById(coinId: String): com.jbappz.cryptocoin.coins.domain.Result<com.jbappz.cryptocoin.coins.data.dto.CoinDetailsResponseDto, com.jbappz.cryptocoin.coins.domain.DataError.Remote>
}