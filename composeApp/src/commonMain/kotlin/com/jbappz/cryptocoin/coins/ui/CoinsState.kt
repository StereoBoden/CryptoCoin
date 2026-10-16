package com.jbappz.cryptocoin.coins.ui

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
data class CoinsState(
    val isLoading: Boolean = false,
    val error: StringResource? = null,
    val coins: List<UiCoinListItem> = emptyList(),
    val chartState: UiChartState? = null,
)

@Stable
data class UiChartState(
    val isLoading: Boolean = false,
    val sparkLine: List<Double> = emptyList(),
    val coinName: String = "",
)