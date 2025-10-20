package com.lanotech.xpost.ui

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LinkWebView(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
