package com.jbappz.cryptocoin.ui

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
data class CoinsState(
    val error: StringResource? = null,
    val coins: List<UiCoinListItem> = emptyList(),
)