package com.jbappz.cryptocoin

import androidx.compose.runtime.Composable
import com.jbappz.cryptocoin.coins.ui.CoinsListScreen
import com.jbappz.cryptocoin.theme.CryptoCoinTheme

@Composable
fun App() {
    CryptoCoinTheme {
        CoinsListScreen(
            onCoinClicked = {
                // TODO: Handle navigation to coin details when implementing detail screen
            }
        )
    }
}
