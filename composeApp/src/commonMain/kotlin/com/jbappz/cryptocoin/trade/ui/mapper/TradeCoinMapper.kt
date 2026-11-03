package com.jbappz.cryptocoin.trade.ui.mapper

import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.trade.ui.common.UITradeCoinItem

/**
 * Maps a UI trade coin item model to a domain [Coin] model.
 *
 * @return A [Coin] domain instance with matching properties.
 */
fun UITradeCoinItem.toCoin() = Coin(
    id = id,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl,
)