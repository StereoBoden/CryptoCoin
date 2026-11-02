package com.jbappz.cryptocoin.trade.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jbappz.cryptocoin.theme.CryptoCoinTheme
import com.jbappz.cryptocoin.theme.LightDarkPreview
import com.jbappz.cryptocoin.theme.LocalExtendedColors
import cryptocoin.composeapp.generated.resources.Res
import cryptocoin.composeapp.generated.resources.error_insufficient_balance
import org.jetbrains.compose.resources.stringResource

@Composable
fun TradeScreen(
    state: TradeState,
    tradeType: TradeType,
    onAmountChange: (String) -> Unit,
    onSubmitClicked: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                AsyncImage(
                    model = state.coin?.iconUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(4.dp).clip(CircleShape).size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = state.coin?.name ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = when (tradeType) {
                    TradeType.BUY -> "Buy Amount"
                    TradeType.SELL -> "Sell Amount"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            CenteredDollarTextField(
                amountText = state.amount,
                onAmountChange = onAmountChange
            )
            Text(
                text = state.availableAmount,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(4.dp)
            )
            if (state.error != null) {
                Text(
                    text = stringResource(state.error),
                    style = MaterialTheme.typography.labelLarge,
                    color = LocalExtendedColors.current.lossRed,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        Button(
            onClick = onSubmitClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = when (tradeType) {
                    TradeType.BUY -> LocalExtendedColors.current.profitGreen
                    TradeType.SELL -> LocalExtendedColors.current.lossRed
                }
            ),
            contentPadding = PaddingValues(horizontal = 64.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        ) {
            Text(
                text = when (tradeType) {
                    TradeType.BUY -> "Buy Now"
                    TradeType.SELL -> "Sell Now"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = when (tradeType) {
                    TradeType.BUY -> MaterialTheme.colorScheme.onPrimary
                    TradeType.SELL -> MaterialTheme.colorScheme.onBackground
                }
            )
        }
    }
}

private val previewTradeCoin = UITradeCoinItem(
    id = "bitcoin",
    name = "Bitcoin",
    symbol = "BTC",
    iconUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png",
    price = 65000.0
)

@LightDarkPreview
@Composable
fun TradeScreenBuyPreview() {
    CryptoCoinTheme {
        TradeScreen(
            state = TradeState(
                coin = previewTradeCoin,
                availableAmount = "Available balance: $10,000.00",
                amount = "250.00"
            ),
            tradeType = TradeType.BUY,
            onAmountChange = {},
            onSubmitClicked = {}
        )
    }
}

@LightDarkPreview
@Composable
fun TradeScreenSellPreview() {
    CryptoCoinTheme {
        TradeScreen(
            state = TradeState(
                coin = previewTradeCoin,
                availableAmount = "Available: 1.5 BTC",
                amount = "0.5"
            ),
            tradeType = TradeType.SELL,
            onAmountChange = {},
            onSubmitClicked = {}
        )
    }
}

@LightDarkPreview
@Composable
fun TradeScreenErrorPreview() {
    CryptoCoinTheme {
        TradeScreen(
            state = TradeState(
                coin = previewTradeCoin,
                availableAmount = "Available balance: $100.00",
                amount = "500.00",
                error = Res.string.error_insufficient_balance
            ),
            tradeType = TradeType.BUY,
            onAmountChange = {},
            onSubmitClicked = {}
        )
    }
}
