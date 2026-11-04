package com.jbappz.cryptocoin.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Portfolio : Route

    @Serializable
    data object CoinsList : Route

    @Serializable
    data class Buy(val coinId: String) : Route

    @Serializable
    data class Sell(val coinId: String) : Route
}
