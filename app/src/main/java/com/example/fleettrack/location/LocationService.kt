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


    private lateinit var apiService: FleetTrackRepository

    private var userId: String? = null
    private var password: String? = null

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
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val param1 = intent?.getStringExtra("userId")
        val param2 = intent?.getStringExtra("password")

        if (param1 != null && param2 != null) {
            userId = param1
            password = param2
        }

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

        locationClient.getLocationUpdates(10000L)
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
            val credentials = UserCredentials(userId!!, password!!)
            val userId = credentials.userid
            val password = credentials.password

            try {
                val result = apiService.sendLocation(locationData(userId, password, arrayOf(latitude, longitude)))
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