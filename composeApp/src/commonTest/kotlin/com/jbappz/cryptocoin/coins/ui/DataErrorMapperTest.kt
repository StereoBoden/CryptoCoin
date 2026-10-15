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
import kotlin.test.Test
import kotlin.test.assertEquals

class DataErrorMapperTest {

    @Test
    fun testLocalErrorDiskFullMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Local.DISK_FULL

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_disk_full, result)
    }

    @Test
    fun testLocalErrorInsufficientFundsMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Local.INSUFFICIENT_FUNDS

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_insufficient_balance, result)
    }

    @Test
    fun testLocalErrorUnknownMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Local.UNKNOWN

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_unknown, result)
    }

    @Test
    fun testRemoteErrorRequestTimeoutMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Remote.REQUEST_TIMEOUT

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_request_timeout, result)
    }

    @Test
    fun testRemoteErrorTooManyRequestsMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Remote.TOO_MANY_REQUESTS

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_too_many_requests, result)
    }

    @Test
    fun testRemoteErrorNoInternetMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Remote.NO_INTERNET

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_no_internet, result)
    }

    @Test
    fun testRemoteErrorServerMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Remote.SERVER

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_unknown, result)
    }

    @Test
    fun testRemoteErrorSerializationMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Remote.SERIALIZATION

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_serialization, result)
    }

    @Test
    fun testRemoteErrorUnknownMapsToCorrectStringResource() {
        // GIVEN
        val error = DataError.Remote.UNKNOWN

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_unknown, result)
    }

    @Test
    fun testGenericErrorMapsToUnknownStringResource() {
        // GIVEN
        val error = object : Error {}

        // WHEN
        val result = error.toStringResource()

        // THEN
        assertEquals(Res.string.error_unknown, result)
    }
}
