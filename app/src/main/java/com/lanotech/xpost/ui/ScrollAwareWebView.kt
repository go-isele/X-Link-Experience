package com.lanotech.xpost.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.MutableStateFlow


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ScrollAwareWebView(
    url: String,
    scrollYFlow: MutableStateFlow<Float>,
    onProgressChange: (Int) -> Unit
) {
    val context = LocalContext.current
    var hasError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    if (hasError) {
        // Show fallback error UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Failed to load page.", color = Color.Red)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { hasError = false; isLoading = true }) {
                    Text("Retry")
                }
            }
        }
    } else {
        AndroidView(
            factory = {
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                            hasError = false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            hasError = true
                            isLoading = false
                        }
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            onProgressChange(newProgress)
                        }
                    }

                    setOnScrollChangeListener { _, _, scrollY, _, _ ->
                        scrollYFlow.value = scrollY.toFloat()
                    }

                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    // Optional: add a full-screen loading overlay
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x33000000)), // semi-transparent overlay
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
