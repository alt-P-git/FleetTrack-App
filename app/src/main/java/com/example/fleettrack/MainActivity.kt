package com.example.fleettrack

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.core.app.ActivityCompat
import com.example.fleettrack.location.LocationService
import com.example.fleettrack.ui.FleetTrackApp
import com.example.fleettrack.ui.screens.WebViewScreen
import com.example.fleettrack.ui.theme.FleetTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )

        enableEdgeToEdge()
        setContent {
            FleetTrackTheme {
                FleetTrackApp(
                    onBackPressed = { finish() },
                    onLocationStart = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                            startService(this)
                        }
                    },
                    onLocationStop = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_STOP
                            startService(this)
                        }
                    }
                )
                /*Column {
                    Button(onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                            startService(this)
                        }
                    }) {
                        Text(text = "Start Tracking")
                    }
                    Button(onClick = {
                        Intent(applicationContext, LocationService::class.java).apply {
                            action = LocationService.ACTION_STOP
                            startService(this)
                        }
                    }) {
                        Text(text = "Stop Tracking")
                    }
                }*/
            }
        }
    }
}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("FleetTrack | WebView", color = Color.White) }) },
        content = { paddingValues -> WebViewScreen(paddingValues) }
    )
}*/