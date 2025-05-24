package com.example.healthcareproject.presentation.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.healthcareproject.data.worker.WorkerScheduler
import timber.log.Timber

@SuppressLint("StaticFieldLeak")
object NetworkMonitor {

    private lateinit var context: Context
    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Timber.tag("NetworkMonitor").d("Network available. Triggering sync worker.")
            WorkerScheduler.scheduleNetworkSyncWorker(context)
        }

        override fun onLost(network: Network) {
            Timber.tag("NetworkMonitor").d("Network lost.")
        }
    }

    private var isMonitoring = false

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun startMonitoring() {
        if (!::context.isInitialized) {
            Timber.tag("NetworkMonitor").e("NetworkMonitor not initialized. Call init(context) first.")
            return
        }
        if (isMonitoring) {
            Timber.tag("NetworkMonitor").d("Already monitoring, skip.")
            return
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        isMonitoring = true
        Timber.tag("NetworkMonitor").d("Started monitoring network.")
    }

    fun stopMonitoring() {
        if (!::context.isInitialized) {
            Timber.tag("NetworkMonitor").e("NetworkMonitor not initialized. Call init(context) first.")
            return
        }
        if (!isMonitoring) {
            Timber.tag("NetworkMonitor").d("Not monitoring, skip stop.")
            return
        }
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            isMonitoring = false
            Timber.tag("NetworkMonitor").d("Stopped monitoring network.")
        } catch (e: Exception) {
            Timber.tag("NetworkMonitor").e(e, "Failed to unregister network callback")
        }
    }
}

