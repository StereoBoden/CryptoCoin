package com.jbappz.cryptocoin.data.mapper

import com.jbappz.cryptocoin.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.data.dto.CoinItemDto
import com.jbappz.cryptocoin.data.dto.CoinPriceDto
import com.jbappz.cryptocoin.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.data.dto.CoinsResponseDto
import com.jbappz.cryptocoin.domain.model.Coin
import com.jbappz.cryptocoin.domain.model.CoinModel
import com.jbappz.cryptocoin.domain.model.CoinPrice

fun CoinItemDto.toCoinModel(): CoinModel {
    return CoinModel(
        coin = Coin(
            id = uuid,
            name = name,
            symbol = symbol,
            iconUrl = iconUrl,
        ),
        price = CoinPrice(
            price = price,
            change = change,
        )
    )
}

fun CoinItemDto.toCoin(): Coin {
    return Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
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
        change = change ?: 0.0,
    )
}

fun CoinPriceHistoryResponseDto.toCoinPrices(): List<CoinPrice> {
    return data.history.map { it.toCoinPrice() }
}
