package com.jbappz.cryptocoin.database.portfolio

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioCoinEntity
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioDao
import com.jbappz.cryptocoin.portfolio.data.local.UserBalanceDao
import com.jbappz.cryptocoin.portfolio.data.local.UserBalanceEntity

@Database(entities = [PortfolioCoinEntity::class, UserBalanceEntity::class], version = 1)
@ConstructedBy(PortfolioDatabaseConstructor::class)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun userBalanceDao(): UserBalanceDao
}