package com.jbappz.cryptocoin.portfolio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Result
import com.jbappz.cryptocoin.coins.ui.toStringResource
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import com.jbappz.cryptocoin.util.formatCoinPercentage
import com.jbappz.cryptocoin.util.formatCoinPrice
import com.jbappz.cryptocoin.util.formatCoinUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

/**
 * [ViewModel] responsible for managing and exposing the UI state of the portfolio screen.
 *
 * This ViewModel coordinates portfolio coin data, total balance, and cash balance retrieved
 * from [PortfolioRepository] and maps them into a unified [PortfolioState] stream for the UI layer.
 *
 * @param portfolioRepository The repository providing access to portfolio data and streams.
 */
class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepository,
): ViewModel() {
    private val _state = MutableStateFlow(PortfolioState(isLoading = true))

    /**
     * A [StateFlow] emitting the UI state for the portfolio screen.
     *
     * Combines local UI state changes, portfolio coin flows, total balance, and cash balance.
     * Triggers balance initialization upon subscriber subscription.
     */
    val state: StateFlow<PortfolioState> = combine(
        _state,
        portfolioRepository.getPortfolioCoinsFlow(),
        portfolioRepository.totalBalanceFlow(),
        portfolioRepository.cashBalanceFlow(),
    ) { currentState, portfolioCoins, totalBalance, cashBalance ->
        when (portfolioCoins) {
            is Result.Success -> {
                handleSuccessState(
                    currentState = currentState,
                    portfolioCoins = portfolioCoins.data,
                    totalBalance = totalBalance,
                    cashBalance = cashBalance,
                )
            }
            is Result.Error -> {
                handleErrorState(
                    currentState = currentState,
                    error = portfolioCoins.error,
                )
            }
        }
    }.onStart {
        portfolioRepository.initialiseBalance()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PortfolioState(isLoading = true),
    )

    /**
     * Handles mapping domain model data into an updated [PortfolioState] on successful retrieval.
     *
     * @param currentState The current [PortfolioState].
     * @param portfolioCoins List of [PortfolioCoinModel] items owned in the portfolio.
     * @param totalBalance The result of the total balance calculation (crypto + cash).
     * @param cashBalance The current cash balance amount.
     * @return An updated [PortfolioState] populated with formatted portfolio data.
     */
    private fun handleSuccessState(
        currentState: PortfolioState,
        portfolioCoins: List<PortfolioCoinModel>,
        totalBalance: Result<Double, DataError>,
        cashBalance: Double,
    ): PortfolioState {
        val portfolioValue = when(totalBalance) {
            is Result.Success -> formatCoinPrice(totalBalance.data)
            is Result.Error<*> -> formatCoinPrice(0.0)
        }

        return currentState.copy(
            coins = portfolioCoins.map { it.toUiPortfolioCoinItem() },
            portfolioValue = portfolioValue,
            cashBalance = formatCoinPrice(cashBalance),
            showBuyButton = portfolioCoins.isNotEmpty(),
            isLoading = false,
        )
    }

    /**
     * Handles mapping domain errors into an updated [PortfolioState] on failure.
     *
     * @param currentState The current [PortfolioState].
     * @param error The domain [DataError] that occurred while loading portfolio coins.
     * @return An updated [PortfolioState] containing the mapped error resource.
     */
    private fun handleErrorState(
        currentState: PortfolioState,
        error: DataError,
    ): PortfolioState {
        return currentState.copy(
            error = error.toStringResource(),
            isLoading = false,
        )
    }

    /**
     * Maps a domain [PortfolioCoinModel] into a UI presentation model [UiPortfolioCoinItem].
     *
     * @return A formatted [UiPortfolioCoinItem] ready for UI display.
     */
    private fun PortfolioCoinModel.toUiPortfolioCoinItem(): UiPortfolioCoinItem =
        UiPortfolioCoinItem(
            id = coin.id,
            name = coin.name,
            iconUrl = coin.iconUrl,
            amountInUnitText = formatCoinUnit(ownedAmountInUnit, coin.symbol),
            amountInFiatText = formatCoinPrice(ownedAmountInFiat),
            performancePercentText = formatCoinPercentage(performancePercent),
            isPositive = performancePercent >= 0,
        )

}