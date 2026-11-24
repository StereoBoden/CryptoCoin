package com.jbappz.cryptocoin.trade.ui.sell

sealed interface SellEvents {
    data object SellSuccess : SellEvents
}