package com.lanotech.xpost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lanotech.xpost.ui.MainScreen
import com.lanotech.xpost.ui.theme.XPostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XPostTheme {
                Surface {
                    MainScreen()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    XPostTheme {
        MainScreen()
    }
}