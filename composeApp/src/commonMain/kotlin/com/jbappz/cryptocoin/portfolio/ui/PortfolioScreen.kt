package com.jbappz.cryptocoin.portfolio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jbappz.cryptocoin.theme.CryptoCoinTheme
import com.jbappz.cryptocoin.theme.LightDarkPreview
import com.jbappz.cryptocoin.theme.extendedColors
import cryptocoin.composeapp.generated.resources.Res
import cryptocoin.composeapp.generated.resources.error_no_internet
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PortfolioScreen(
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    val portfolioViewModel = koinViewModel<PortfolioViewModel>()
    val state by portfolioViewModel.state.collectAsStateWithLifecycle()
    PortfolioContent(
        state = state,
        onCoinItemClicked = onCoinItemClicked,
        onDiscoverCoinsClicked = onDiscoverCoinsClicked,
    )
}

@Composable
fun PortfolioContent(
    state: PortfolioState,
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inversePrimary)
    ) {
        if (state.isLoading) {
            LoadingContent()
        } else if (state.error != null) {
//            ErrorContent(state.error)
        } else {
            PortfolioBalance(
                portfolioValue = state.portfolioValue,
                cashBalance = state.cashBalance,
                showBuyButton = state.showBuyButton,
                onBuyButtonClicked = onDiscoverCoinsClicked,
            )
            PortfolioCoinsList(
                coins = state.coins,
                onCoinItemClicked = onCoinItemClicked,
                onDiscoverCoinsClicked = onDiscoverCoinsClicked,
            )
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.extendedColors.profitGreen,
            modifier = Modifier.size(32.dp)
        )
    }
}

//@Composable
//fun ErrorContent(errorStringRes: StringResource) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = stringResource(errorStringRes),
//            color = MaterialTheme.colorScheme.error,
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.bodyLarge,
//        )
//    }
//}

@Composable
fun PortfolioBalance(
    portfolioValue: String,
    cashBalance: String,
    showBuyButton: Boolean,
    onBuyButtonClicked: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight(0.3f)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inversePrimary)
            .padding(32.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Total Value:",
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = portfolioValue,
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            )
            Row {
                Text(
                    text = "Cash Balance: ",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = cashBalance,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                )
            }
            if (showBuyButton) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onBuyButtonClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.extendedColors.profitGreen,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 64.dp,
                    )
                ) {
                    Text(
                        text = "Buy Coin",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun PortfolioCoinsList(
    coins: List<UiPortfolioCoinItem>,
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (coins.isEmpty()) {
            PortfolioEmpty(
                onDiscoverCoinsClicked = onDiscoverCoinsClicked,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "💰 Owned Coins",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(coins) { coin ->
                        PortfolioCoinListItem(
                            coin = coin,
                            onCoinItemClicked = onCoinItemClicked,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PortfolioCoinListItem(
    coin: UiPortfolioCoinItem,
    onCoinItemClicked: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCoinItemClicked(coin.id) }
            .padding(16.dp)
    ) {
        AsyncImage(
            model = coin.iconUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = coin.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = coin.amountInUnitText,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = coin.amountInFiatText,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = coin.performancePercentText,
                color = if (coin.isPositive) MaterialTheme.extendedColors.profitGreen else MaterialTheme.extendedColors.lossRed,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
            )
        }
    }
}

@Composable
fun PortfolioEmpty(
    onDiscoverCoinsClicked: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "Your portfolio is empty",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onDiscoverCoinsClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.extendedColors.profitGreen,
            )
        ) {
            Text(
                text = "Discover Coins",
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

private val previewPortfolioCoin1 = UiPortfolioCoinItem(
    id = "bitcoin",
    name = "Bitcoin",
    iconUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
    amountInUnitText = "0.5 BTC",
    amountInFiatText = "$32,716.05",
    performancePercentText = "+2.45%",
    isPositive = true,
)

private val previewPortfolioCoin2 = UiPortfolioCoinItem(
    id = "ethereum",
    name = "Ethereum",
    iconUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
    amountInUnitText = "2.0 ETH",
    amountInFiatText = "$6,913.56",
    performancePercentText = "-1.20%",
    isPositive = false,
)

private val previewPortfolioCoin3 = UiPortfolioCoinItem(
    id = "solana",
    name = "Solana",
    iconUrl = "https://assets.coingecko.com/coins/images/4128/large/solana.png",
    amountInUnitText = "15.0 SOL",
    amountInFiatText = "$2,182.50",
    performancePercentText = "+5.82%",
    isPositive = true,
)

@LightDarkPreview
@Composable
fun PortfolioContentLoadingPreview() {
    CryptoCoinTheme {
        PortfolioContent(
            state = PortfolioState(isLoading = true),
            onCoinItemClicked = {},
            onDiscoverCoinsClicked = {},
        )
    }
}

@LightDarkPreview
@Composable
fun PortfolioContentPreview() {
    CryptoCoinTheme {
        PortfolioContent(
            state = PortfolioState(
                portfolioValue = "$41,812.11",
                cashBalance = "$5,000.00",
                showBuyButton = true,
                coins = listOf(
                    previewPortfolioCoin1,
                    previewPortfolioCoin2,
                    previewPortfolioCoin3,
                ),
            ),
            onCoinItemClicked = {},
            onDiscoverCoinsClicked = {},
        )
    }
}

@LightDarkPreview
@Composable
fun PortfolioContentEmptyPreview() {
    CryptoCoinTheme {
        PortfolioContent(
            state = PortfolioState(
                portfolioValue = "$5,000.00",
                cashBalance = "$5,000.00",
                showBuyButton = false,
                coins = emptyList(),
            ),
            onCoinItemClicked = {},
            onDiscoverCoinsClicked = {},
        )
    }
}

@LightDarkPreview
@Composable
fun PortfolioContentErrorPreview() {
    CryptoCoinTheme {
        PortfolioContent(
            state = PortfolioState(error = Res.string.error_no_internet),
            onCoinItemClicked = {},
            onDiscoverCoinsClicked = {},
        )
    }
}
