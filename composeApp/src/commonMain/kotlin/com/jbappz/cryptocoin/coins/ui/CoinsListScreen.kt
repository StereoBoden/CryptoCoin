package com.jbappz.cryptocoin.coins.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jbappz.cryptocoin.coins.ui.component.PerformanceChart
import com.jbappz.cryptocoin.theme.CryptoCoinTheme
import com.jbappz.cryptocoin.theme.LightDarkPreview
import com.jbappz.cryptocoin.theme.extendedColors
import cryptocoin.composeapp.generated.resources.Res
import cryptocoin.composeapp.generated.resources.error_no_internet
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CoinsListScreen(
    onCoinClicked: (String) -> Unit,
) {
    val coinsListViewModel = koinViewModel<CoinListViewModel>()
    val state by coinsListViewModel.state.collectAsStateWithLifecycle()

    CoinsListContent(
        state = state,
        onCoinClicked = onCoinClicked,
        onCoinLongPressed = { coinId -> coinsListViewModel.onCoinLongPressed(coinId) },
        onDismissChart = { coinsListViewModel.onDismissChart() },
    )
}

@Composable
fun CoinsListContent(
    state: CoinsState,
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onDismissChart: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingContent(innerPadding)
        } else if (state.error != null) {
           ErrorContent(innerPadding, state.error)
        } else {
            CoinsContent(
                state = state,
                innerPadding = innerPadding,
                onCoinClicked = onCoinClicked,
                onCoinLongPressed = onCoinLongPressed,
                onDismissChart = onDismissChart,
            )
        }
    }
}

@Composable
fun LoadingContent(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorContent(innerPadding: PaddingValues, errorStringRes: StringResource) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(errorStringRes),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun CoinsContent(
    state: CoinsState,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onDismissChart: () -> Unit,
) {
    if (state.chartState != null) {
        CoinChartDialog(
            uiChartState = state.chartState,
            onDismiss = onDismissChart,
        )
    }
    CoinsList(
        coins = state.coins,
        innerPadding = innerPadding,
        onCoinClicked = onCoinClicked,
        onCoinLongPressed = onCoinLongPressed,
    )
}

@Composable
fun CoinsList(
    coins: List<UiCoinListItem>,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text(
                text = "🔥 Top Coins:",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(coins) { coin ->
            CoinListItem(
                coin = coin,
                onCoinClicked = onCoinClicked,
                onCoinLongPressed = onCoinLongPressed,
            )
        }
    }
}

@Composable
private fun CoinListItem(
    coin: UiCoinListItem,
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = { onCoinLongPressed(coin.id) },
                onClick = { onCoinClicked(coin.id) }
            )
            .padding(16.dp)
    ) {
        AsyncImage(
            model = coin.iconUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(4.dp).clip(CircleShape).size(40.dp)
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
                text = coin.symbol,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = coin.formattedPrice,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = coin.formattedChange,
                color = if (coin.isPositive) MaterialTheme.extendedColors.profitGreen else MaterialTheme.extendedColors.lossRed,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
            )
        }
    }
}

@Composable
fun CoinChartDialog(
    uiChartState: UiChartState,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "24h Price chart for ${uiChartState.coinName}",
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center,
            ) {
                if (uiChartState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                } else {
                    PerformanceChart(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        nodes = uiChartState.sparkLine,
                        profitColor = MaterialTheme.extendedColors.profitGreen,
                        lossColor = MaterialTheme.extendedColors.lossRed,
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(
                    text = "Close",
                )
            }
        }
    )
}

private val previewCoin1 = UiCoinListItem(
    id = "bitcoin",
    name = "Bitcoin",
    symbol = "BTC",
    iconUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
    formattedPrice = "$65,432.10",
    formattedChange = "+2.45%",
    isPositive = true,
)

private val previewCoin2 = UiCoinListItem(
    id = "ethereum",
    name = "Ethereum",
    symbol = "ETH",
    iconUrl = "https://assets.coingecko.com/coins/images/279/large/ethereum.png",
    formattedPrice = "$3,456.78",
    formattedChange = "-1.20%",
    isPositive = false,
)

private val previewCoin3 = UiCoinListItem(
    id = "solana",
    name = "Solana",
    symbol = "SOL",
    iconUrl = "https://assets.coingecko.com/coins/images/4128/large/solana.png",
    formattedPrice = "$145.50",
    formattedChange = "+5.82%",
    isPositive = true,
)

@LightDarkPreview
@Composable
fun CoinsListContentLoadingPreview() {
    CryptoCoinTheme {
        CoinsListContent(
            state = CoinsState(isLoading = true),
            onCoinClicked = {},
            onCoinLongPressed = {},
            onDismissChart = {},
        )
    }
}

@LightDarkPreview
@Composable
fun CoinsListContentPreview() {
    CryptoCoinTheme {
        CoinsListContent(
            state = CoinsState(
                coins = listOf(
                    previewCoin1,
                    previewCoin2,
                    previewCoin3
                )
            ),
            onCoinClicked = {},
            onCoinLongPressed = {},
            onDismissChart = {},
        )
    }
}

@LightDarkPreview
@Composable
fun CoinsListContentChartPositivePreview() {
    CryptoCoinTheme {
        CoinsListContent(
            state = CoinsState(
                coins = listOf(
                    previewCoin1,
                ),
                chartState = UiChartState(
                    isLoading = false,
                    sparkLine = listOf(10.0, 15.0, 12.0, 18.0, 20.0, 17.0, 25.0),
                    coinName = previewCoin1.name
                )
            ),
            onCoinClicked = {},
            onCoinLongPressed = {},
            onDismissChart = {},
        )
    }
}

@LightDarkPreview
@Composable
fun CoinsListContentChartNegativePreview() {
    CryptoCoinTheme {
        CoinsListContent(
            state = CoinsState(
                coins = listOf(
                    previewCoin2,
                ),
                chartState = UiChartState(
                    isLoading = false,
                    sparkLine = listOf(25.0, 15.0, 12.0, 18.0, 20.0, 17.0, 2.0),
                    coinName = previewCoin2.name
                )
            ),
            onCoinClicked = {},
            onCoinLongPressed = {},
            onDismissChart = {},
        )
    }
}

@LightDarkPreview
@Composable
fun CoinsListContentErrorPreview() {
    CryptoCoinTheme {
        CoinsListContent(
            state = CoinsState(error = Res.string.error_no_internet),
            onCoinClicked = {},
            onCoinLongPressed = {},
            onDismissChart = {},
        )
    }
}
