package com.jbappz.cryptocoin.trade.ui.common

/**
 * MVI Actions / Intents representing user interactions on trading screens (Buy and Sell).
 */
sealed interface TradeAction {
    data class OnAmountChanged(val amount: String) : TradeAction
    data object OnSubmitClicked : TradeAction
}
