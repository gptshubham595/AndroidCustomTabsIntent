package com.shubham.androidcustomtabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shubham.androidcustomtabs.ui.theme.AndroidCustomTabsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidCustomTabsTheme {
                BrowserDemoApp()
            }
        }
    }
}