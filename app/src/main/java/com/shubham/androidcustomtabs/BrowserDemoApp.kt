package com.shubham.androidcustomtabs

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

private const val DEFAULT_URL = "http://10.0.2.2:3000"

data class DemoSession(
    val name: String,
    val email: String,
    val source: String,
    val transport: String
)

@Composable
fun BrowserDemoApp() {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val keyboardController = LocalSoftwareKeyboardController.current

    var baseUrl by remember { mutableStateOf(DEFAULT_URL) }
    var showWebView by remember { mutableStateOf(false) }
    var webViewUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var currentSession by remember { mutableStateOf<DemoSession?>(null) }

    val customTabsManager = remember(context) { CustomTabsPerformanceManager(context) }
    val customTabsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val session = extractSessionFromUri(it.data?.data)
        if (session != null) {
            currentSession = session
            errorMessage = ""
        }
    }

    DisposableEffect(customTabsManager) {
        customTabsManager.bind()
        onDispose { customTabsManager.unbind() }
    }

    LaunchedEffect(activity?.deepLinkIntent) {
        extractSessionFromUri(activity?.deepLinkIntent?.data)?.let {
            currentSession = it
            errorMessage = ""
            context.toast("Custom Tabs returned to app for ${it.email}")
        }
    }

    if (showWebView) {
        WebViewScreen(
            url = webViewUrl,
            onBack = { showWebView = false },
            onSessionUpdate = { currentSession = it }
        )
    } else {
        HomeScreen(
            url = baseUrl,
            onUrlChange = {
                baseUrl = it
                errorMessage = ""
            },
            errorMessage = errorMessage,
            session = currentSession,
            onCustomTabsClick = {
                val result = openWithCustomTabs(
                    keyboardController = keyboardController,
                    context = context,
                    url = buildCustomTabsLoginUrl(baseUrl),
                    performanceManager = customTabsManager,
                    launcher = customTabsLauncher
                )
                errorMessage = result ?: ""
            },
            onWebViewClick = {
                val result = openWithWebView(
                    keyboardController = keyboardController,
                    url = buildLoginDemoUrl(baseUrl)
                )
                if (result.errorMessage != null) {
                    errorMessage = result.errorMessage
                } else if (result.webViewUrl != null) {
                    webViewUrl = result.webViewUrl
                    showWebView = true
                }
            },
            onClearSession = { currentSession = null }
        )
    }
}

fun buildCustomTabsLoginUrl(input: String): String {
    val base = buildLoginDemoUrl(input)
    return "${base}?callbackUrl=androidcustomtabs%3A%2F%2Flogin"
}

fun openWithCustomTabs(
    keyboardController: SoftwareKeyboardController?,
    context: Context,
    url: String,
    performanceManager: CustomTabsPerformanceManager,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>
): String? {
    val finalUrl = normalizeUrl(url)
    val targetUri = finalUrl.toUri()
    return try {
        performanceManager.prepareUrl(targetUri)

        val builder = CustomTabsIntent.Builder(performanceManager.getSession())
        builder.setShowTitle(true)
        val primaryColor = ContextCompat.getColor(context, R.color.teal_200)
        builder.setToolbarColor(primaryColor)
        val secondaryColor = ContextCompat.getColor(context, R.color.purple_200)
        builder.setSecondaryToolbarColor(secondaryColor)
        builder.setUrlBarHidingEnabled(true)
        builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        builder.setInstantAppsEnabled(false)
        builder.setStartAnimations(
            context,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        builder.setExitAnimations(
            context,
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )

        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(primaryColor)
            .build()

        builder.setDefaultColorSchemeParams(params)
        builder.setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)

        val customTabsIntent = builder.build().apply {
            intent.`package` = performanceManager.getPackageName()
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.data = targetUri
        }

        performanceManager.markLaunchStarted(finalUrl)
        launcher.launch(customTabsIntent.intent)
        keyboardController?.hide()

        context.toast(
            "Launching SSO demo with Custom Tabs. After success, the web page will deep-link back into the app."
        )
        null
    } catch (e: Exception) {
        keyboardController?.hide()
        "Could not open Custom Tab: ${e.localizedMessage ?: "Unknown error"}"
    }
}

data class WebViewResult(
    val webViewUrl: String? = null,
    val errorMessage: String? = null
)

fun openWithWebView(
    keyboardController: SoftwareKeyboardController?,
    url: String
): WebViewResult {
    val finalUrl = normalizeUrl(url)
    keyboardController?.hide()
    return WebViewResult(webViewUrl = finalUrl)
}

@Preview(showBackground = true, name = "BrowserDemoApp - Home")
@Composable
private fun BrowserDemoAppPreview_Home() {
    BrowserDemoApp()
}
