@file:JvmName("CommonModuleKt")
package com.jbappz.cryptocoin.di

import kotlin.jvm.JvmName
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModule
        )
    }
}

expect val platformModule: Module
