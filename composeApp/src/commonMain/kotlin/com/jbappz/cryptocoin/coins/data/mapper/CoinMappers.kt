package com.jbappz.cryptocoin.coins.data.mapper

import com.jbappz.cryptocoin.coins.data.dto.CoinItemDto
import com.jbappz.cryptocoin.coins.data.dto.CoinPriceDto
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

fun CoinPriceDto.toPriceModel() = PriceModel(
    price = price ?: 0.0,
    timestamp = timestamp,
)