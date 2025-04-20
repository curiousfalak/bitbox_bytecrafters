package com.example.mltest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.example.mltest.alert.ThreatFormScreen
import com.example.mltest.navigation.Botsheet
import com.example.mltest.screens.HomeScreen
import com.example.mltest.ui.theme.MltestTheme

class MainActivity : ComponentActivity() {

    private val showThreatForm = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if this activity was launched from a notification
        if (intent?.getBooleanExtra("navigate_to_form", false) == true) {
            showThreatForm.value = true
        }

        setContent {
            MltestTheme {
                if (showThreatForm.value) {
                    // Show the ThreatFormScreen if flag is true
                    ThreatFormScreen()
                } else {
                    // Your normal app UI
                    Botsheet(this)
                }
            }
        }
    }
}




