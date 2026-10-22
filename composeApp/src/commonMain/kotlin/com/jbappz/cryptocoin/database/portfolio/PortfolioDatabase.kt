package com.jbappz.cryptocoin.database.portfolio

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioCoinEntity
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioDao

@Database(entities = [PortfolioCoinEntity::class], version = 1)
@ConstructedBy(PortfolioDatabaseConstructor::class)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
}