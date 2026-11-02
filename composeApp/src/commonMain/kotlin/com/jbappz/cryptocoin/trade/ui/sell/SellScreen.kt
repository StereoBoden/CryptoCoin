package com.jbappz.cryptocoin.trade.ui.sell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jbappz.cryptocoin.trade.ui.common.TradeScreen
import com.jbappz.cryptocoin.trade.ui.common.TradeType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SellScreen(
    coinId: String,
    navigateToPortfolio: () -> Unit,
) {
    val viewModel = koinViewModel<SellViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    //TODO: handle coinId

    TradeScreen(
        state = state,
        tradeType = TradeType.SELL,
        onAmountChange = viewModel::onAmountChanged,
        onSubmitClicked = viewModel::onSellClicked
    )
}