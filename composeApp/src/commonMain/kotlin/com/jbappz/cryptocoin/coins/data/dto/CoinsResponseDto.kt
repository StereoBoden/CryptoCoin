package com.jbappz.cryptocoin.coins.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinsResponseDto(
    val data: com.jbappz.cryptocoin.coins.data.dto.CoinsListDto
)

@Serializable
data class CoinsListDto(
    val coins: List<com.jbappz.cryptocoin.coins.data.dto.CoinItemDto>
)

@Serializable
data class CoinItemDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    val price: Double,
    val rank: Int,
    val change: Double,
)