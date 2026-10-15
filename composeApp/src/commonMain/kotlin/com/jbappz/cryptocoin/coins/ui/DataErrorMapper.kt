package com.jbappz.cryptocoin.coins.ui

import com.jbappz.cryptocoin.coins.domain.DataError
import com.jbappz.cryptocoin.coins.domain.Error
import cryptocoin.composeapp.generated.resources.Res
import cryptocoin.composeapp.generated.resources.error_disk_full
import cryptocoin.composeapp.generated.resources.error_insufficient_balance
import cryptocoin.composeapp.generated.resources.error_no_internet
import cryptocoin.composeapp.generated.resources.error_request_timeout
import cryptocoin.composeapp.generated.resources.error_serialization
import cryptocoin.composeapp.generated.resources.error_too_many_requests
import cryptocoin.composeapp.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

fun DataError.toStringResource(): StringResource {
    return when (this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.INSUFFICIENT_FUNDS -> Res.string.error_insufficient_balance
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.SERVER -> Res.string.error_unknown
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
    }
}

fun Error.toStringResource(): StringResource {
    return when (this) {
        is DataError -> this.toStringResource()
        else -> Res.string.error_unknown
    }
}
