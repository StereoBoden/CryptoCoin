package com.jbappz.cryptocoin.trade.ui.buy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbappz.cryptocoin.trade.ui.common.TradeScreen
import com.jbappz.cryptocoin.trade.ui.common.TradeType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BuyScreen(
    coinId: String,
    navigateToPortfolio: () -> Unit,
) {
    val viewModel = koinViewModel<BuyViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    //TODO: handle coinId

    TradeScreen(
        state = state,
        tradeType = TradeType.BUY,
        onAmountChange = viewModel::onAmountChanged,
        onSubmitClicked = viewModel::onBuyClicked
    )
}