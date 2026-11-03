package com.jbappz.cryptocoin

import androidx.compose.runtime.Composable
import com.jbappz.cryptocoin.portfolio.ui.PortfolioScreen
import com.jbappz.cryptocoin.theme.CryptoCoinTheme

@Composable
fun App() {
    CryptoCoinTheme {
        PortfolioScreen(
            onCoinItemClicked = {
                // TODO: Handle navigation to coin details when implementing detail screen
            },
            onDiscoverCoinsClicked = {
                // TODO: Handle navigation to discover coins screen
            }
        )

//        CoinsListScreen(
//            onCoinClicked = {
//                // TODO: Handle navigation to coin details when implementing detail screen
//            }
//        )
//
//        BuyScreen(
//            coinId = "1",
//            navigateToPortfolio = {
//                // TODO: Handle navigation back to portfolio
//            }
//        )
//        SellScreen(
//            coinId = "1",
//            navigateToPortfolio = {
//                // TODO: Handle navigation back to portfolio
//            }
//        )
    }
}
