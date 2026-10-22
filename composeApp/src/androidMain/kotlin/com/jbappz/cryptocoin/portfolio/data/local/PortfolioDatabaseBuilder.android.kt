package com.jbappz.cryptocoin.portfolio.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jbappz.cryptocoin.database.portfolio.PortfolioDatabase

fun getPortfolioDatabaseBuilder(context: Context): RoomDatabase.Builder<PortfolioDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("portfolio.db")
    return Room.databaseBuilder<PortfolioDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
