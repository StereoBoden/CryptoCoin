@file:JvmName("CommonModuleKt")
package com.jbappz.cryptocoin.di

import com.jbappz.cryptocoin.coins.data.KtorCoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.data.network.HttpClientFactory
import com.jbappz.cryptocoin.coins.domain.CoinsRemoteDataSource
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinDetailsUseCase
import com.jbappz.cryptocoin.coins.domain.usecase.GetCoinsListUseCase
import com.jbappz.cryptocoin.coins.ui.CoinListViewModel
import io.ktor.client.HttpClient
import kotlin.jvm.JvmName
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
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
    viewModel { CoinListViewModel(get()) }
    singleOf(::GetCoinsListUseCase)
    singleOf(::KtorCoinsRemoteDataSource).bind<CoinsRemoteDataSource>()
    singleOf(::GetCoinDetailsUseCase)
}
