package com.shubham.androidcustomtabs

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession

class CustomTabsPerformanceManager(private val context: Context) {
    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null
    private var serviceConnection: CustomTabsServiceConnection? = null
    private var browserPackage: String? = null
    private var launchStartTimeMs: Long = 0L
    private var launchUrl: String? = null
    private var navStartedAtMs: Long? = null
    private var firstPaintAtMs: Long? = null

    fun bind() {
        if (serviceConnection != null) return

        val packageName = CustomTabsClient.getPackageName(context, null) ?: return
        browserPackage = packageName

        serviceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                customTabsClient = client
                client.warmup(0L)
                customTabsSession = createSession(client)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                customTabsClient = null
                customTabsSession = null
            }
        }

        CustomTabsClient.bindCustomTabsService(context, packageName, serviceConnection!!)
    }

    private fun createSession(client: CustomTabsClient): CustomTabsSession? {
        return client.newSession(object : CustomTabsCallback() {
            override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                val now = SystemClock.elapsedRealtime()
                when (navigationEvent) {
                    CustomTabsCallback.NAVIGATION_STARTED -> {
                        if (launchStartTimeMs > 0L) {
                            navStartedAtMs = now - launchStartTimeMs
                        }
                    }

                    CustomTabsCallback.TAB_SHOWN -> {
                        if (launchStartTimeMs > 0L && firstPaintAtMs == null) {
                            firstPaintAtMs = now - launchStartTimeMs
                        }
                    }

                    CustomTabsCallback.NAVIGATION_FINISHED -> {
                        if (launchStartTimeMs > 0L) {
                            val visibleMs = firstPaintAtMs ?: (now - launchStartTimeMs)
                            val navigationMs = navStartedAtMs
                            val fullMs = now - launchStartTimeMs
                            val navPart = navigationMs?.let { "NAV ${it}ms" } ?: "NAV --"
                            val paintPart = "paint ${visibleMs}ms"
                            val fullPart = "full ${fullMs}ms"
                            context.toast(
                                "Custom Tabs metrics captured\n" +
                                        "$navPart · $paintPart · $fullPart\n" +
                                        "Shared browser process/cookies reduce app-side startup work."
                            )
                            launchStartTimeMs = 0L
                            launchUrl = null
                            navStartedAtMs = null
                            firstPaintAtMs = null
                        }
                    }
                }
            }
        })
    }

    fun prepareUrl(url: Uri) {
        if (customTabsSession == null) {
            customTabsClient?.let { customTabsSession = createSession(it) }
        }
        customTabsSession?.mayLaunchUrl(url, null, null)
    }

    fun getSession(): CustomTabsSession? = customTabsSession

    fun getPackageName(): String? = browserPackage

    fun markLaunchStarted(url: String) {
        launchStartTimeMs = SystemClock.elapsedRealtime()
        launchUrl = url
        navStartedAtMs = null
        firstPaintAtMs = null
    }

    fun unbind() {
        serviceConnection?.let {
            runCatching { context.unbindService(it) }
        }
        serviceConnection = null
        customTabsClient = null
        customTabsSession = null
        navStartedAtMs = null
        firstPaintAtMs = null
    }
}