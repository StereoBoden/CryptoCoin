package com.jbappz.cryptocoin

import androidx.compose.ui.window.ComposeUIViewController
import com.jbappz.cryptocoin.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }