package com.example.fleettrack.ui.screens

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun HomeScreen(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
) {
    /*if(windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
        TripsScreen()
    } else {
        WebViewScreen()
    }*/
}