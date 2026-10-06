package com.jbappz.cryptocoin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage

data class CoinRoutineColorsPalette(
    val profitGreen: Color = Color(0xFF2E7D32),
    val lossRed: Color = Color(0xFFD32F2F),
)

val LocalCoinRoutineColorsPalette = staticCompositionLocalOf {
    CoinRoutineColorsPalette()
}

@Composable
fun CoinsListScreen(
    onCoinClicked: (String) -> Unit,
) {
    val coinsListViewModel = viewModel(CoinListViewModel::class) // Todo: we need to change this after we implement DI
    val state by coinsListViewModel.state.collectAsStateWithLifecycle()

    CoinsListContent(
        state = state,
        onCoinClicked = onCoinClicked
    )
}

@Composable
fun CoinsListContent(
    state: CoinsState,
    onCoinClicked: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CoinsList(
            coins = state.coins,
            onCoinClicked = onCoinClicked
        )
    }
}

@Composable
fun CoinsList(
    coins: List<UiCoinListItem>,
    onCoinClicked: (String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
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
                    onCoinClicked = onCoinClicked
                )
            }
        }
    }
}

@Composable
fun CoinListItem(
    coin: UiCoinListItem,
    onCoinClicked: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCoinClicked(coin.id) }
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
                color = if (coin.isPositive) LocalCoinRoutineColorsPalette.current.profitGreen else LocalCoinRoutineColorsPalette.current.lossRed,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
            )
        }
    }
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

@Preview
@Composable
fun CoinsListContentPreview() {
    MaterialTheme {
        CoinsListContent(
            state = CoinsState(
                coins = listOf(previewCoin1, previewCoin2, previewCoin3)
            ),
            onCoinClicked = {}
        )
    }
}

@Preview
@Composable
fun CoinsListContentEmptyPreview() {
    MaterialTheme {
        CoinsListContent(
            state = CoinsState(coins = emptyList()),
            onCoinClicked = {}
        )
    }
}
