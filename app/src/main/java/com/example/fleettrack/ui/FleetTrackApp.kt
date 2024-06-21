package com.example.fleettrack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fleettrack.ui.screens.FleetTrackViewModel
import com.example.fleettrack.ui.screens.LoadingScreen
import com.example.fleettrack.ui.screens.LoginScreen
import com.example.fleettrack.ui.screens.TripsScreen
import com.example.fleettrack.ui.screens.WebViewScreen

@Composable
fun FleetTrackApp(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
) {
    val viewModel: FleetTrackViewModel = viewModel(
        factory = FleetTrackViewModel.Factory
    )
    val uiState = viewModel.uiState.collectAsState().value
    val isLoggedIn = uiState.isLoggedIn
    val loginFailed = uiState.loginFailed

    if(isLoggedIn == null) {
        LoadingScreen()
    } else if(isLoggedIn == true) {
        if(uiState.isShowingTripsScreen)
            TripsScreen()
        else
            WebViewScreen(
                onBackPressed = { viewModel.navigateToTripsScreen() },
                modifier = modifier
            )
    } else if(loginFailed) {
        LoginScreen(
            modifier = modifier,
            failedAttempt = true
        )
    } else {
        LoginScreen(
            modifier = modifier
        )
    }
}