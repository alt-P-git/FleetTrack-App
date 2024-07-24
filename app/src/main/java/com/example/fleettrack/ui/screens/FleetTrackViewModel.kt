package com.example.fleettrack.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fleettrack.data.LocalTripDataProvider
import com.example.fleettrack.data.PreferencesRepository
import com.example.fleettrack.model.UserCredentials
import com.example.fleettrack.FleetTrackApp
import com.example.fleettrack.data.FleetTrackRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.math.log

class FleetTrackViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val fleetTrackRepository: FleetTrackRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(
        FleetTrackUiState(
            tripList = LocalTripDataProvider.getTripList(),
            currentTrip = LocalTripDataProvider.getTripList().firstOrNull(),
            isLoggedIn = null
        )
    )
    val uiState: StateFlow<FleetTrackUiState> = _uiState
    lateinit var credentials: UserCredentials

    init {
        viewModelScope.launch {
            val isLoggedIn = preferencesRepository.isLoggedIn.first()
            delay(1000)
            _uiState.update { currentState ->
                currentState.copy(isLoggedIn = isLoggedIn)
            }
            credentials = preferencesRepository.userCredentials.first()
        }
    }

    fun updateCurrentTrip(selectedTripId: String) {
        _uiState.update {
            it.copy(currentTrip = it.tripList.find { trip -> trip.tripID == selectedTripId })
        }
    }

    fun navigateToTripsScreen() {
        _uiState.update {
            it.copy(isShowingTripsScreen = true)
        }
    }

    fun navigateToWebView() {
        _uiState.update {
            it.copy(isShowingTripsScreen = false)
        }
    }

    fun loginAndCheckCredentials(credentials: UserCredentials) {
        /*viewModelScope.launch {
            preferencesRepository.saveLoggedIn(true, credentials)
            _uiState.update { currentState ->
                currentState.copy(isLoggedIn = true)
            }
        }*/
        viewModelScope.launch {
            try {
                val checkDriverResult = fleetTrackRepository.checkCredentials(credentials.userid, credentials.password)
                val loggedIn = checkDriverResult.isValid
//                Log.e("FleetTrackViewModel", "Result: $checkDriverResult")
                preferencesRepository.saveLoggedIn(loggedIn, credentials)
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoggedIn = loggedIn,
                        loginFailed = !loggedIn
                    )
                }
            } catch (e: Exception) {
//                Log.e("FleetTrackViewModel", "Error logging in.", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoggedIn = false,
                        loginFailed = true
                    )
                }
            }
        }
    }


    suspend fun getCredentials(): UserCredentials {
        return preferencesRepository.userCredentials.first()
    }

    fun getTrips() {
        _uiState.update { currentState ->
            currentState.copy(
                isTripsLoading = true,
                isTripsLoadingError = false
            )
        }
        /*viewModelScope.launch {
            delay(1000)
            try {
                val trips = LocalTripDataProvider.getTripList()
                _uiState.update { currentState ->
                    currentState.copy(tripList = trips, isTripsLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(isTripsLoadingError = true)
                }
            }
        }*/
        viewModelScope.launch {
            try {
                val trips = fleetTrackRepository.getTrips(credentials.userid, credentials.password)
//                Log.e("FleetTrackViewModel", "Trips: $trips")
                _uiState.update { currentState ->
                    currentState.copy(tripList = trips, isTripsLoading = false)
                }
            } catch (e: Exception) {
                Log.e("FleetTrackViewModel", "Error getting trips.", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        isTripsLoading = false,
                        isTripsLoadingError = true
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.saveLoggedIn(false, UserCredentials("", ""))
            _uiState.update { currentState ->
                currentState.copy(isLoggedIn = false)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FleetTrackApp)
                FleetTrackViewModel(
                    application.preferencesRepository,
                    application.fleetTrackRepository
                )
            }
        }
    }

    /*private fun initializeUIState() {
        _uiState.value = FleetTrackUiState()
    }*/
}