package com.jbappz.cryptocoin.coins.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinsListUseCase
import com.jbappz.cryptocoin.util.formatCoinPrice
import com.jbappz.cryptocoin.util.formatCoinPercentage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CoinListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart {
            getAllCoins()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState(),
        )

    private suspend fun getAllCoins() {
        when(val coinsResponse = getCoinsListUseCase.execute()) {
            is Result.Success -> {
                _state.update {
                    CoinsState(
                        coins = coinsResponse.data.map { coinItem ->
                            UiCoinListItem(
                                id = coinItem.coin.id,
                                name = coinItem.coin.name,
                                symbol = coinItem.coin.symbol,
                                iconUrl = coinItem.coin.iconUrl,
                                formattedPrice = formatCoinPrice(coinItem.coinPrice.price),
                                formattedChange = formatCoinPercentage(coinItem.coinPrice.change),
                                isPositive = coinItem.coinPrice.change >= 0
                            )
                        }
                    )
                }
            }
            is Result.Error -> {
                _state.update {
                    it.copy(
                        coins = emptyList(),
                        error = null, // TODO: Update error
                    )
                }
            }
        }
    }
}
