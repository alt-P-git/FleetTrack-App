package com.example.fleettrack.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fleettrack.R
import com.example.fleettrack.model.Trip
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onLocationStart: () -> Unit = {},
    onLocationStop: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: FleetTrackViewModel = viewModel(
        factory = FleetTrackViewModel.Factory
    )
    val uiState by viewModel.uiState.collectAsState()

    val rotation: Float by animateFloatAsState(
        targetValue = if (uiState.isTripsLoading) 360f else 0f,
        animationSpec = if (uiState.isTripsLoading) {
            infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        } else {
            tween(durationMillis = 0)
        }
    )
    val openDialog = remember { mutableStateOf(false) }

    var locationStatus = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getTrips()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FleetTrack | Trips") },
                actions = {
                    IconButton(onClick = { viewModel.getTrips() }) {
                        Icon(
                            painterResource(id = R.drawable.refresh),
                            contentDescription = "Refresh",
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                    if(locationStatus.value)
                    {
                        IconButton(onClick = {
                            locationStatus.value = false;
                            onLocationStop()
                        }) {
                            Icon(
                                painterResource(id = R.drawable.location_on),
                                contentDescription = "Location On"
                            )
                        }
                    };
                    if(!locationStatus.value)
                    {
                        IconButton(onClick = {
                            locationStatus.value = true;
                            onLocationStart()
                        }) {
                            Icon(
                                painterResource(id = R.drawable.location_off),
                                contentDescription = "Location Off"
                            )
                        }
                    };
                    IconButton(onClick = { openDialog.value = true}) {
                        Icon(painterResource(id = R.drawable.logout), contentDescription = "Logout")
                    }
                }
            )
        },
        content = { paddingValues -> if(uiState.isTripsLoading) {
            LoadingScreen(paddingValues = paddingValues)
        } else if(uiState.isTripsLoadingError) {
            CenterText(
                text = "Failed to load trips",
                paddingValues = paddingValues
            )
        } else if(uiState.tripList.isEmpty()) {
            CenterText(
                text = "No trips found",
                paddingValues = paddingValues
            )
        } else {
            TripsList(
                trips = uiState.tripList,
                paddingValues = paddingValues,
                selectedTripId = { tripId -> viewModel.updateCurrentTrip(tripId) },
                navigateToWebView = { viewModel.navigateToWebView() },
            )
        }

            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDialog.value = false },
                    title = { Text(text = "Logout") },
                    text = { Text("Are you sure you want to logout?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                                viewModel.logout()
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { openDialog.value = false }
                        ) {
                            Text("No")
                        }
                    }
                )
            }

        }
    )
}

@Composable
fun TripsList(
    trips: List<Trip>,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    selectedTripId: (String) -> Unit = {},
    navigateToWebView: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(paddingValues)
    ) {
        trips.forEach { trip ->
            TripItem(
                trip = trip,
                navigateToWebView = navigateToWebView,
                selectedTripId = selectedTripId
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripItem(
    trip: Trip,
    selectedTripId: (String) -> Unit = {},
    navigateToWebView: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val startDateTimeParts = trip.startDateTime
    val endDateTimeParts = trip.tripEndDataTime

    //convert date time which is in mills to date and time
    val startDate = startDateTimeParts.let { it ->
        val date = java.util.Date(it)
        val sdf = java.text.SimpleDateFormat("dd-MM-yyyy")
        sdf.format(date)
    }
    val startTime = startDateTimeParts.let { it ->
        val date = java.util.Date(it)
        val sdf = java.text.SimpleDateFormat("HH:mm")
        sdf.format(date)
    }
    val endDate = endDateTimeParts.let { it ->
        val date = java.util.Date(it)
        val sdf = java.text.SimpleDateFormat("dd-MM-yyyy")
        sdf.format(date)
    }
    val endTime = endDateTimeParts.let { it ->
        val date = java.util.Date(it)
        val sdf = java.text.SimpleDateFormat("HH:mm")
        sdf.format(date)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = trip.tripID,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "Status: ${trip.tripStatus}",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 12.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = startDate,
                        fontSize = 14.sp
                    )
                    Text(
                        text = startTime,
                        fontSize = 14.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "---")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${trip.estimatedTime/3600}h ${trip.estimatedTime/60}m",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${trip.distance} kms",
                            fontSize = 12.sp
                        )
                    }
                    Text(text = "---")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = endDate,
                        fontSize = 14.sp
                    )
                    Text(
                        text = endTime,
                        fontSize = 14.sp
                    )
                }
            }
            Button(
                onClick = {
                    selectedTripId(trip.tripID);
                    navigateToWebView();
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp),
            ) {
                Text(
                    text = "View Route",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun CenterText(
    text: String,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
        )
    }
}

/*
@Preview
@Composable
fun PreviewTripItem() {
    TripItem(
        trip = Trip(
            id = 1,
            name = "Trip 1",
            distance = 10.0,
            duration = 60,
            startDateTime = "2021-10-01 10:00",
            endDataTime = "2021-10-01 11:00"
        )
    )
}*/
