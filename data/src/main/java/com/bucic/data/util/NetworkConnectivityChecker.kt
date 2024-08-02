package com.bucic.data.util

interface NetworkConnectivityChecker {
    fun isNetworkAvailable(): Boolean
}