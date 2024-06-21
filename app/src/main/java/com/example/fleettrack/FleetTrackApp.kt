package com.example.fleettrack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.fleettrack.data.AppContainer
import com.example.fleettrack.data.DefaultAppContainer
import com.example.fleettrack.data.FleetTrackRepository
import com.example.fleettrack.data.PreferencesRepository

private const val PREFERENCES_REPOSITORY_NAME = "preferences_repository"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_REPOSITORY_NAME
)

class FleetTrackApp: Application() {
    lateinit var preferencesRepository: PreferencesRepository
    lateinit var fleetTrackRepository: FleetTrackRepository
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()

        preferencesRepository = PreferencesRepository(dataStore)
        fleetTrackRepository = container.fleetTrackRepository

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Fleettrack",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}