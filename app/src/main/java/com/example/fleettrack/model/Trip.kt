package com.example.fleettrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*@Serializable
data class Trip(
    val id: Int,
    val name: String,
    val distance: Double,
    val duration: Int,
    @SerialName("start_date_time")
    val startDateTime: String,
    @SerialName("end_date_time")
    val endDataTime: String,
)*/

@Serializable
data class Trip(
    @SerialName("_id")
    val id: String,
    val userID: String,
    val driverId: String,
    @SerialName("tripId")
    val tripID: String,
    val routeName: String,
    val vehicleId: String,
    @SerialName("scheduled_date_time")
    val startDateTime: String,//date
    @SerialName("trip_start_date_time")
    val tripStartDataTime: String,//date
    @SerialName("trip_end_date_time")
    val tripEndDataTime: String,//date
    @SerialName("last_route_point_index")
    val lastRoutePointIndex: Int,
    val tripStatus: String,
    @SerialName("estimatedTime")
    val estimatedTime: Int,
    @SerialName("distance")
    val distance: Double,
)
