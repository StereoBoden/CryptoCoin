package com.jbappz.cryptocoin.coins.domain
sealed interface DataError : com.jbappz.cryptocoin.coins.domain.Error {
    enum class Remote : com.jbappz.cryptocoin.coins.domain.DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN
    }

    enum class Local : com.jbappz.cryptocoin.coins.domain.DataError {
        DISK_FULL,
        INSUFFICIENT_FUNDS,
        UNKNOWN
    }
}