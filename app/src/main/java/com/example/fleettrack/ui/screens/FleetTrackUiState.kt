package com.example.fleettrack.ui.screens

import com.example.fleettrack.model.Trip

data class FleetTrackUiState(
    val isShowingHomepage: Boolean = true,
    val isShowingTripsScreen: Boolean = true,
    val isShowingWebView: Boolean = false,
    val tripList: List<Trip> = emptyList(),
    val currentTrip: Trip? = null,
    val isLoggedIn: Boolean? = null,
    val loginFailed: Boolean = false,
    val isTripsLoading: Boolean = false,
    val isTripsLoadingError: Boolean = false
) {
}