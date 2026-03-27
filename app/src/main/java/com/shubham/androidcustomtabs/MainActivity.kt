package com.shubham.androidcustomtabs

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.shubham.androidcustomtabs.ui.theme.AndroidCustomTabsTheme

class MainActivity : ComponentActivity() {
    var deepLinkIntent by mutableStateOf<Intent?>(null)
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deepLinkIntent = intent
        enableEdgeToEdge()
        setContent {
            AndroidCustomTabsTheme {
                BrowserDemoApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkIntent = intent
    }
}
