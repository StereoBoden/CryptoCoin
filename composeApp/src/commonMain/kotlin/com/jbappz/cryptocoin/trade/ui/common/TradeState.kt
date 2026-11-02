package com.jbappz.cryptocoin.trade.ui.common

import org.jetbrains.compose.resources.StringResource

data class TradeState(
    val isLoading: Boolean = false,
    val error: StringResource? = null,
    val availableAmount: String = "",
    val amount: String = "",
    val coin: UITradeCoinItem? = null,
)