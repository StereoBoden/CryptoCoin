package com.jbappz.cryptocoin.portfolio.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserBalanceDao {
    @Query("SELECT cashBalance From UserBalanceEntity")
    suspend fun getUserBalance(): Double?

    @Upsert
    suspend fun insertBalance(userBalanceEntity: UserBalanceEntity)

    @Query("UPDATE UserBalanceEntity SET cashBalance = :newBalance WHERE id = 1")
    suspend fun updateCashBalance(newBalance: Double)
}