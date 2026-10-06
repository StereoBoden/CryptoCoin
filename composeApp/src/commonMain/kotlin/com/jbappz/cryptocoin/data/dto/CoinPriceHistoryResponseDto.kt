package com.jbappz.cryptocoin.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceHistoryResponseDto(
    val data: CoinPriceHistoryDto
)

@Serializable
data class CoinPriceHistoryDto(
    val history: List<CoinPriceDto>
)

@Serializable
data class CoinPriceDto(
    val price: Double?,
    val change: Double?,
)