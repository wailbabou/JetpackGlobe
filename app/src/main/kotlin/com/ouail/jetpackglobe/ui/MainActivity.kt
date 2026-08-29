package com.ouail.jetpackglobe.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ouail.jetpackglobe.ui.theme.JetpackGlobeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackGlobeTheme {
                GlobeShowcaseScreen()
            }
        }
    }
}