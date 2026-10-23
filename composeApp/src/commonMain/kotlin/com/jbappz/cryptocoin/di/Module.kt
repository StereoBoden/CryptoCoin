@file:JvmName("CommonModuleKt")
package com.jbappz.cryptocoin.di

import androidx.room.RoomDatabase
import com.jbappz.cryptocoin.coins.data.KtorCoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.data.network.HttpClientFactory
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinDetailsUseCase
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinPriceHistoryUseCase
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinsListUseCase
import com.jbappz.cryptocoin.coins.ui.CoinListViewModel
import com.jbappz.cryptocoin.database.portfolio.PortfolioDatabase
import com.jbappz.cryptocoin.database.portfolio.getPortfolioDatabase
import com.jbappz.cryptocoin.portfolio.data.PortfolioRepositoryImpl
import com.jbappz.cryptocoin.portfolio.data.local.PortfolioDao
import com.jbappz.cryptocoin.portfolio.domain.PortfolioRepository
import io.ktor.client.HttpClient
import kotlin.jvm.JvmName
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule,
            platformModule
        )
    }
}

expect val platformModule: Module

val sharedModule = module {
    single<HttpClient> { HttpClientFactory.create(get()) }

    // Coins List
    viewModelOf(::CoinListViewModel)
    singleOf(::GetCoinsListUseCase)
    singleOf(::GetCoinPriceHistoryUseCase)
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
    singleOf(::GetCoinDetailsUseCase)

    // Portfolio Repository
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()

    // Portfolio Database
    single<PortfolioDatabase> {
        getPortfolioDatabase(get<RoomDatabase.Builder<PortfolioDatabase>>())
    }
    single<PortfolioDao> {
        get<PortfolioDatabase>().portfolioDao()
    }
}
