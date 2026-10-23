package com.jbappz.cryptocoin.portfolio.data.mapper

import com.jbappz.cryptocoin.coins.domain.model.Coin
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioCoinEntity
import com.jbappz.cryptocoin.portfolio.domain.PortfolioCoinModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PortfolioEntityMapperTest {

    @Test
    fun testToPortfolioCoinModelMappingAndPerformanceCalculation() {
        val entity = PortfolioCoinEntity(
            coinId = "btc-id",
            name = "Bitcoin",
            symbol = "BTC",
            iconUrl = "https://example.com/btc.png",
            averagePurchasePrice = 50000.0,
            amountOwned = 0.5,
            timestamp = 1234567890L,
        )
        val currentPrice = 60000.0

        val model = entity.toPortfolioCoinModel(currentPrice)

        assertEquals("btc-id", model.coin.id)
        assertEquals("Bitcoin", model.coin.name)
        assertEquals("BTC", model.coin.symbol)
        assertEquals("https://example.com/btc.png", model.coin.iconUrl)
        assertEquals(50000.0, model.averagePurchasePrice)
        assertEquals(0.5, model.ownedAmountInUnit)
        assertEquals(30000.0, model.ownedAmountInFiat)
        assertEquals(20.0, model.performancePercent)
    }

    @Test
    fun testToPortfolioCoinModelNegativePerformance() {
        val entity = PortfolioCoinEntity(
            coinId = "eth-id",
            name = "Ethereum",
            symbol = "ETH",
            iconUrl = "https://example.com/eth.png",
            averagePurchasePrice = 3000.0,
            amountOwned = 2.0,
            timestamp = 1234567890L,
        )
        val currentPrice = 2400.0

        val model = entity.toPortfolioCoinModel(currentPrice)

        assertEquals(-20.0, model.performancePercent)
        assertEquals(4800.0, model.ownedAmountInFiat)
    }

    @Test
    fun testToPortfolioCoinEntityMapping() {
        val model = PortfolioCoinModel(
            coin = Coin(
                id = "sol-id",
                name = "Solana",
                symbol = "SOL",
                iconUrl = "https://example.com/sol.png",
            ),
            performancePercent = 15.0,
            averagePurchasePrice = 100.0,
            ownedAmountInUnit = 10.0,
            ownedAmountInFiat = 1150.0,
        )

        val entity = model.toPortfolioCoinEntity()

        assertEquals("sol-id", entity.coinId)
        assertEquals("Solana", entity.name)
        assertEquals("SOL", entity.symbol)
        assertEquals("https://example.com/sol.png", entity.iconUrl)
        assertEquals(100.0, entity.averagePurchasePrice)
        assertEquals(10.0, entity.amountOwned)
        assertTrue(entity.timestamp > 0)
    }
}
