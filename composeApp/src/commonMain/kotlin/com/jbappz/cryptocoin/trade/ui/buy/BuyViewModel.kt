package com.jbappz.cryptocoin.trade.ui.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinDetailsUseCase
import com.jbappz.cryptocoin.coins.ui.toStringResource
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import com.jbappz.cryptocoin.trade.domain.BuyCoinUseCase
import com.jbappz.cryptocoin.trade.ui.common.TradeState
import com.jbappz.cryptocoin.trade.ui.common.UITradeCoinItem
import com.jbappz.cryptocoin.trade.ui.mapper.toCoin
import com.jbappz.cryptocoin.util.formatCoinPrice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing UI state and user interactions for buying cryptocurrency.
 *
 * @property getCoinDetailsUseCase Use case to retrieve coin details and price.
 * @property portFolioRepository Repository to observe cash balance.
 * @property buyCoinUseCase Use case to perform the coin purchase.
 */
class BuyViewModel(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val portFolioRepository: PortfolioRepository,
    private val buyCoinUseCase: BuyCoinUseCase,
): ViewModel() {
    private val coinId = "1" // TODO: Remove when navigation implemented
    private val _amount = MutableStateFlow("")
    private val _state = MutableStateFlow(TradeState())

    /**
     * StateFlow exposing the current UI state of the Buy screen.
     */
    val state = combine(
        _state,
        _amount
    ) {
        state, amount ->
        state.copy(
            amount = amount,
        )
    }.onStart {
        val balance = portFolioRepository.cashBalanceFlow().firstOrNull()
        balance?.let {
            getCoinDetails(it)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TradeState(isLoading = true)
    )

    private suspend fun getCoinDetails(balance: Double) {
        when(val coinResponse = getCoinDetailsUseCase.execute(coinId)) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        coin = UITradeCoinItem(
                            id = coinResponse.data.coin.id,
                            name = coinResponse.data.coin.name,
                            symbol = coinResponse.data.coin.symbol,
                            iconUrl = coinResponse.data.coin.iconUrl,
                            price = coinResponse.data.price,
                        ),
                        availableAmount = "Available: ${formatCoinPrice(balance)}"
                    )
                }
            }
            is Result.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = coinResponse.error.toStringResource()
                    )
                }
            }
        }
    }

    /**
     * Updates the purchase amount entered by the user.
     *
     * @param amount The new fiat amount string.
     */
    fun onAmountChanged(amount: String) {
        _amount.value = amount
    }

    /**
     * Triggers the buy operation using the currently selected coin and entered amount.
     */
    fun onBuyClicked() {
        val tradeCoin = state.value.coin ?: return
        viewModelScope.launch {
            val buyCoinResponse = buyCoinUseCase.buyCoin(
                coin = tradeCoin.toCoin(),
                amountInFiat = _amount.value.toDouble(),
                price = tradeCoin.price,
            )

            when (buyCoinResponse) {
                is Result.Success -> {
                    // TODO: Navigate to next screen with event
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = buyCoinResponse.error.toStringResource(),
                        )
                    }
                }
            }
        }
    }
}