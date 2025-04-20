package com.example.mltest.geotagged

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@SuppressLint("WrongConstant")
@Composable
fun HideSystemBarsEffect() {
    val context = LocalContext.current
    val window = (context as? Activity)?.window ?: return

    DisposableEffect(Unit) {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.hide(WindowInsets.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            // Restore system bars when leaving
            WindowCompat.setDecorFitsSystemWindows(window, true)
            controller.show(WindowInsets.Type.systemBars())
        }
    }
}
