package com.aaa.vibesmusic.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private fun networkCallback(callback: (ConnectionStatus) -> Unit): ConnectivityManager.NetworkCallback =
    object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionStatus.AVAILABLE)
        }

        override fun onLost(network: Network) {
            callback(ConnectionStatus.UNAVAILABLE)
        }
    }

fun getCurrentConnectionStatus(connectivityManager: ConnectivityManager): ConnectionStatus {
    val network: Network? = connectivityManager.activeNetwork

    val isConnected: Boolean = connectivityManager
        .getNetworkCapabilities(network)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

    return if(isConnected) ConnectionStatus.AVAILABLE else ConnectionStatus.UNAVAILABLE
}

fun Context.observeConnectivityAsFlow(): Flow<ConnectionStatus> = callbackFlow {
    val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val callback: ConnectivityManager.NetworkCallback = networkCallback { connectionStatus ->
        trySend(connectionStatus)
    }

    val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(networkRequest, callback)

    val currentConnectionStatus: ConnectionStatus = getCurrentConnectionStatus(connectivityManager)
    trySend(currentConnectionStatus)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

val Context.currentConnectionStatus: ConnectionStatus
    get() {
        val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectionStatus(connectivityManager)
    }

@Composable
fun rememberConnectionStatus(): State<ConnectionStatus> {
    val context: Context = LocalContext.current

    return produceState(initialValue = context.currentConnectionStatus) {
        context.observeConnectivityAsFlow().collect {
            value = it
        }
    }
}