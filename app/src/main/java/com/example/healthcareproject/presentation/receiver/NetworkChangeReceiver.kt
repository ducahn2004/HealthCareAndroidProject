package com.example.healthcareproject.presentation.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.healthcareproject.data.worker.WorkerScheduler
import timber.log.Timber

class NetworkChangeReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent?) {
        if (isNetworkAvailable(context)) {
            WorkerScheduler.scheduleNetworkSyncWorker(context)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        Timber.tag("NetworkChangeReceiver").d("Active network: $network")
        Timber.tag("NetworkChangeReceiver").d("Network capabilities: $capabilities")

        val isAvailable = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        Timber.tag("NetworkChangeReceiver").d("Is network available: $isAvailable")

        return isAvailable
    }
}