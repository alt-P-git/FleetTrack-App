package com.example.fleettrack.location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.fleettrack.R
import com.example.fleettrack.data.AppContainer
import com.example.fleettrack.data.DefaultAppContainer
import com.example.fleettrack.data.FleetTrackRepository
import com.example.fleettrack.data.NetworkFleetTrackRepository
import com.example.fleettrack.data.PreferencesRepository
import com.example.fleettrack.model.UserCredentials
import com.example.fleettrack.model.locationData
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    lateinit var container: AppContainer


    //initialize NetworkFleetTrackRepository
    private lateinit var apiService: FleetTrackRepository
    private val PREFERENCES_REPOSITORY_NAME = "preferences_repository"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_REPOSITORY_NAME
    )
    lateinit var preferencesRepository: PreferencesRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

        container = DefaultAppContainer()
        apiService = container.fleetTrackRepository
        preferencesRepository = PreferencesRepository(dataStore)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(30000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val updatedNotification = notification.setContentText("Location: ${location.latitude}, ${location.longitude}")
                notificationManager.notify(1, updatedNotification.build())
                sendLocationToApi(location.latitude, location.longitude)
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun sendLocationToApi(
        latitude: Double,
        longitude: Double
    ) {
        serviceScope.launch {
            val credentials : UserCredentials = preferencesRepository.userCredentials.first()
            val userId = credentials.userid
            val password = credentials.password

            try {
                val result = apiService.sendLocation(locationData(userId, password, arrayOf(latitude, longitude)))
                Log.d("LocationService", "Location sent")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}