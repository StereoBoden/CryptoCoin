package com.jbappz.cryptocoin.trade.ui.sell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinDetailsUseCase
import com.jbappz.cryptocoin.coins.ui.toStringResource
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import com.jbappz.cryptocoin.trade.domain.SellCoinUseCase
import com.jbappz.cryptocoin.trade.ui.common.TradeState
import com.jbappz.cryptocoin.trade.ui.common.UITradeCoinItem
import com.jbappz.cryptocoin.trade.ui.mapper.toCoin
import com.jbappz.cryptocoin.util.formatCoinPrice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SellViewModel(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val portFolioRepository: PortfolioRepository,
    private val sellCoinUseCase: SellCoinUseCase,
): ViewModel() {
    private val coinId = "1" // TODO: Remove when navigation implemented
    private val _amount = MutableStateFlow("")
    private val _state = MutableStateFlow(TradeState())

    val state = combine(
        _state,
        _amount
    ) {
            state, amount ->
        state.copy(
            amount = amount,
        )
    }.onStart {
        val portfolioCoinResponse = portFolioRepository.getPortfolioCoin(coinId)
        when (portfolioCoinResponse) {
            is Result.Success -> {
                portfolioCoinResponse.data?.ownedAmountInUnit?.let {
                    getCoinDetails(it)
                }
            }
            is Result.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = portfolioCoinResponse.error.toStringResource()
                    )
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TradeState(isLoading = true)
    )

    fun onAmountChanged(amount: String) {
        _amount.value = amount
    }

    private suspend fun getCoinDetails(ownedAmountInUnit: Double) {
        when (val coinResponse = getCoinDetailsUseCase.execute(coinId)) {
            is Result.Success -> {
                val availableAmountInFiat = ownedAmountInUnit * coinResponse.data.price
                _state.update {
                    it.copy(
                        coin = UITradeCoinItem(
                            id = coinResponse.data.coin.id,
                            name = coinResponse.data.coin.name,
                            symbol = coinResponse.data.coin.symbol,
                            iconUrl = coinResponse.data.coin.iconUrl,
                            price = coinResponse.data.price,
                        ),
                        availableAmount = "Available: ${formatCoinPrice(availableAmountInFiat)}"
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

        fun onSellClicked() {
            val tradeCoin = state.value.coin ?: return
            viewModelScope.launch {
                val sellCoinResponse = sellCoinUseCase.sellCoin(
                    coin = tradeCoin.toCoin(),
                    amountInFiat = _amount.value.toDouble(),
                    price = tradeCoin.price
                )
                when (sellCoinResponse) {
                    is Result.Success -> {
                        // TODO: add event and navigation
                    }

                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = sellCoinResponse.error.toStringResource()
                            )
                        }
                    }
                }
            }
        }
    }
}