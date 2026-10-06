package com.jbappz.cryptocoin.data.mapper

import com.jbappz.cryptocoin.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.data.dto.CoinItemDto
import com.jbappz.cryptocoin.data.dto.CoinPriceDto
import com.jbappz.cryptocoin.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.data.dto.CoinsResponseDto
import com.jbappz.cryptocoin.domain.model.Coin
import com.jbappz.cryptocoin.domain.model.CoinPrice

fun CoinItemDto.toCoin(): Coin {
    return Coin(
        uuid = uuid,
        symbol = symbol,
        name = name,
        iconUrl = iconUrl,
        price = price,
        rank = rank,
        change = change,
    )
}

fun CoinsResponseDto.toCoins(): List<Coin> {
    return data.coins.map { it.toCoin() }
}

fun CoinDetailsResponseDto.toCoin(): Coin {
    return data.coin.toCoin()
}

fun CoinPriceDto.toCoinPrice(): CoinPrice {
    return CoinPrice(
        price = price ?: 0.0,
        timestamp = timestamp,
    )
}

fun CoinPriceHistoryResponseDto.toCoinPrices(): List<CoinPrice> {
    return data.history.map { it.toCoinPrice() }
}
