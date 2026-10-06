package com.jbappz.cryptocoin.coins.ui

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
data class CoinsState(
    val error: StringResource? = null,
    val coins: List<com.jbappz.cryptocoin.coins.ui.UiCoinListItem> = emptyList(),
)