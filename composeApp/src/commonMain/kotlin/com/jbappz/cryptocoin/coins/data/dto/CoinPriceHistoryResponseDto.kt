package com.jbappz.cryptocoin.coins.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceHistoryResponseDto(
    val data: com.jbappz.cryptocoin.coins.data.dto.CoinPriceHistoryDto
)

@Serializable
data class CoinPriceHistoryDto(
    val history: List<com.jbappz.cryptocoin.coins.data.dto.CoinPriceDto>
)

@Serializable
data class CoinPriceDto(
    val price: Double?,
    val change: Double?,
)