package com.jbappz.cryptocoin.trade.ui.common

import org.jetbrains.compose.resources.StringResource

/**
 * Represents the UI state for coin trading screens (Buy and Sell).
 *
 * @property isLoading Indicates whether data is currently loading.
 * @property error An optional resource for error messages displayed to the user.
 * @property availableAmount A display string indicating the available balance or coin holdings.
 * @property amount The trade amount input by the user.
 * @property coin The UI representation of the coin being traded, or `null` if not loaded.
 */
data class TradeState(
    val isLoading: Boolean = false,
    val error: StringResource? = null,
    val availableAmount: String = "",
    val amount: String = "",
    val coin: UITradeCoinItem? = null,
)