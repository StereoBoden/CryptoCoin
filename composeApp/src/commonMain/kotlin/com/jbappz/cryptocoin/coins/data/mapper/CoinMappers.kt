package com.jbappz.cryptocoin.coins.data.mapper

import com.jbappz.cryptocoin.coins.data.dto.CoinDetailsResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinItemDto
import com.jbappz.cryptocoin.coins.data.dto.CoinPriceHistoryResponseDto
import com.jbappz.cryptocoin.coins.data.dto.CoinsResponseDto
import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.coins.domain.model.CoinModel
import com.jbappz.cryptocoin.coins.domain.model.PriceModel

fun CoinItemDto.toCoinModel(): CoinModel {
    return CoinModel(
        coin = Coin(
            id = uuid,
            name = name,
            symbol = symbol,
            iconUrl = iconUrl,
        ),
        price = price?.toDoubleOrNull() ?: 0.0,
        change = change?.toDoubleOrNull() ?: 0.0,
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

fun CoinPriceHistoryResponseDto.toPriceModels(): List<PriceModel> {
    return data.history.mapNotNull {
        val priceDouble = it.price?.toDoubleOrNull()
        if (priceDouble != null && it.timestamp != null) {
            PriceModel(
                price = priceDouble,
                timestamp = it.timestamp,
            )
        } else null
    }
}
