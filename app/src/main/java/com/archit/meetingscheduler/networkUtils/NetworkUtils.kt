package com.archit.meetingscheduler.networkUtils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.NetworkCapabilities
import android.net.NetworkRequest



object NetworkUtils: ConnectivityManager.NetworkCallback(){

    var isConnected: Boolean = false

    private var networkRequest: NetworkRequest? = null
    var connectivityManager: ConnectivityManager? = null

    init {
        networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
    }

    fun enableConnectivityManager(context: Context) {
        connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.registerNetworkCallback(networkRequest, this)
    }

    fun disableConnectivityManager(){
        connectivityManager?.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        isConnected = true
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isConnected = false
    }
}