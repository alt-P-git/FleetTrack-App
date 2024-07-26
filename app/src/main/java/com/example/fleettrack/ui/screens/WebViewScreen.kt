package com.example.fleettrack.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
){
    val viewModel: FleetTrackViewModel = viewModel(
        factory = FleetTrackViewModel.Factory
    )
    val uiState by viewModel.uiState.collectAsState()

    val mUrl = "https://fleet-track-0.vercel.app/ViewDriverRoutes?routeId=${uiState.currentTrip?.routeId}"
    val webView = remember { mutableStateOf<WebView?>(null) }

    BackHandler {
        onBackPressed()
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
        ,
        factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.setGeolocationEnabled(true)
                webChromeClient = object : WebChromeClient() {
                    override fun onGeolocationPermissionsShowPrompt(
                        origin: String,
                        callback: GeolocationPermissions.Callback
                    ) {
                        callback.invoke(origin, true, false)
                    }
                }
                webViewClient = CustomWebViewClient()
                loadUrl(mUrl)
                webView.value = this

                postDelayed({
                    loadUrl(mUrl)
                }, 2000)
            }
        }, update = {
            it.loadUrl(mUrl)
        })
}

class CustomWebViewClient: WebViewClient(){
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return false
    }
}