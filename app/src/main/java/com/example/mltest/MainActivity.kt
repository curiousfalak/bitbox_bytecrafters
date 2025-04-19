package com.example.mltest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mltest.aquatic_species_mlmodel.ClassifierScreen
import com.example.mltest.ui.theme.MltestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MltestTheme {
                ClassifierScreen()

                }
            }
        }
    }


