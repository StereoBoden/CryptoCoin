package com.jbappz.cryptocoin

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jbappz.cryptocoin.coins.ui.CoinsListScreen
import com.jbappz.cryptocoin.theme.CryptoCoinTheme

@Composable
@Preview
fun App() {
    CryptoCoinTheme {
        CoinsListScreen(
            onCoinClicked = {
                // TODO: Handle navigation to coin details when implementing detail screen
            }
        )
    }
}
