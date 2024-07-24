package com.example.fleettrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    @SerialName("_id")
    val id: String,
    val userID: String,
    @SerialName("tripId")
    val tripID: String,
    val routeName: String,
    val routeId: String,
    val vehicleId: String,
    @SerialName("scheduled_date_time")
    val startDateTime: Long,
    @SerialName("trip_start_date_time")
    val tripStartDataTime: Long,
    @SerialName("trip_end_date_time")
    val tripEndDataTime: Long,
    @SerialName("last_route_point_index")
    val lastRoutePointIndex: Int,
    val tripStatus: String,
    val estimatedTime: Long,
    val distance: Double,
)