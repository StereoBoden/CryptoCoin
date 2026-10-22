@file:JvmName("AndroidModuleKt")
package com.jbappz.cryptocoin.di

import androidx.room.RoomDatabase
import com.jbappz.cryptocoin.database.portfolio.PortfolioDatabase
import com.jbappz.cryptocoin.portfolio.data.local.getPortfolioDatabaseBuilder
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()
}
