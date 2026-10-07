@file:JvmName("CommonModuleKt")
package com.jbappz.cryptocoin.di

import com.jbappz.cryptocoin.coins.data.network.HttpClientFactory
import io.ktor.client.HttpClient
import kotlin.jvm.JvmName
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
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
}
