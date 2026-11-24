package com.jbappz.cryptocoin.trade.ui

sealed interface TradeEvent {
    data object NavigateToPortfolio : TradeEvent
}
