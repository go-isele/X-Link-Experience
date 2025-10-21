package com.lanotech.xpost

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import com.lanotech.xpost.ui.MainScreen
import com.lanotech.xpost.ui.theme.XPostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val view = LocalView.current
            XPostTheme {
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as Activity).window
                        setStatusBar(
                            window = window,
                            view = view,
                            color = Color.Transparent.copy(0.5f)
                        )
                    }
                }
                Surface {
                    MainScreen()
                }
            }
        }
    }
}




fun setStatusBar(
    window: Window,
    view: View,
    color: Color
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        // Android 15+
        window.decorView.setOnApplyWindowInsetsListener { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
            v.setBackgroundColor(color.toArgb())
            v.setPadding(0, statusBarInsets.top, 0, 0)
            insets
        }
    } else {
        @Suppress("DEPRECATION")
        window.statusBarColor = color.toArgb()
    }

    // Pick icon color automatically based on luminance
    val insetsController = WindowCompat.getInsetsController(window, view)
    val useDarkIcons = color.luminance() > 0.5f
    insetsController.isAppearanceLightStatusBars = useDarkIcons
}