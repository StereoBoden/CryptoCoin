package com.jbappz.cryptocoin.portfolio.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import com.jbappz.cryptocoin.database.portfolio.PortfolioDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSUserDomainMask

fun getPortfolioDatabaseBuilder(): RoomDatabase.Builder<PortfolioDatabase> {
    val dbFilePath = NSHomeDirectory() + "/portfolio.db"
    return Room.databaseBuilder<PortfolioDatabase>(
        name = dbFilePath
    )
}