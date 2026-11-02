package com.jbappz.cryptocoin.trade.ui.common

/**
 * UI model representing a coin item on trading screens.
 *
 * @property id Unique identifier for the coin.
 * @property name Display name of the coin (e.g., "Bitcoin").
 * @property symbol Ticker symbol of the coin (e.g., "BTC").
 * @property iconUrl Image URL for the coin logo.
 * @property price Current market price per unit of the coin.
 */
data class UITradeCoinItem(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val price: Double,
)
