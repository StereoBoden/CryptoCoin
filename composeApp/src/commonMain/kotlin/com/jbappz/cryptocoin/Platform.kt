package com.jbappz.cryptocoin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform