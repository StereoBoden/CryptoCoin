package com.jbappz.cryptocoin.trade.ui.buy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.jbappz.cryptocoin.trade.ui.TradeEvent
import com.jbappz.cryptocoin.trade.ui.common.TradeScreen
import com.jbappz.cryptocoin.trade.ui.common.TradeType
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BuyScreen(
    coinId: String,
    navigateToPortfolio: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel = koinViewModel<BuyViewModel>(
        parameters = {
            parametersOf(coinId)
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel.events) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    TradeEvent.NavigateToPortfolio -> navigateToPortfolio()
                }
            }
        }
    }

    TradeScreen(
        state = state,
        tradeType = TradeType.BUY,
        onAction = viewModel::onAction,
    )
}
