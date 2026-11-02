package com.jbappz.cryptocoin.trade.ui.mapper

import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.trade.ui.common.UITradeCoinItem

fun UITradeCoinItem.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl,
)