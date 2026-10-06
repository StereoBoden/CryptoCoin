package com.jbappz.cryptocoin.coins.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailsResponseDto(
    val data: com.jbappz.cryptocoin.coins.data.dto.CoinResponseDto,
)

@Serializable
data class CoinResponseDto(
    val coin: com.jbappz.cryptocoin.coins.data.dto.CoinItemDto,
)