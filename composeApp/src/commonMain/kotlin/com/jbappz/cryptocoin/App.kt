package com.jbappz.cryptocoin

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jbappz.cryptocoin.coins.ui.CoinsListScreen
import com.jbappz.cryptocoin.navigation.Route
import com.jbappz.cryptocoin.portfolio.ui.PortfolioScreen
import com.jbappz.cryptocoin.theme.CryptoCoinTheme
import com.jbappz.cryptocoin.trade.ui.buy.BuyScreen
import com.jbappz.cryptocoin.trade.ui.sell.SellScreen

@Composable
fun App() {
    CryptoCoinTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Route.Portfolio
        ) {
            composable<Route.Portfolio> {
                PortfolioScreen(
                    onCoinItemClicked = { coinId ->
                        navController.navigate(Route.Sell(coinId))
                    },
                    onDiscoverCoinsClicked = {
                        navController.navigate(Route.CoinsList)
                    }
                )
            }

            composable<Route.CoinsList> {
                CoinsListScreen(
                    onCoinClicked = { coinId ->
                        navController.navigate(Route.Buy(coinId))
                    }
                )
            }

            composable<Route.Buy> { backStackEntry ->
                val coinId = backStackEntry.toRoute<Route.Buy>().coinId
                BuyScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Route.Portfolio) {
                            popUpTo<Route.Portfolio> { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.Sell> { backStackEntry ->
                val coinId = backStackEntry.toRoute<Route.Sell>().coinId
                SellScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Route.Portfolio) {
                            popUpTo<Route.Portfolio> { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
