package com.jbappz.cryptocoin.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailsResponseDto(
    val data: CoinResponseDto,
)

@Serializable
data class CoinResponseDto(
    val coin: CoinItemDto,
)