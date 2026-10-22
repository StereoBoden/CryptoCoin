package com.jbappz.cryptocoin.database.portfolio

import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Suppress("KotlinNoActualForExpect")
expect object PortfolioDatabaseConstructor : RoomDatabaseConstructor<PortfolioDatabase> {
    override fun initialize(): PortfolioDatabase
}

fun getPortfolioDatabase(
    builder: RoomDatabase.Builder<PortfolioDatabase>
): PortfolioDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
