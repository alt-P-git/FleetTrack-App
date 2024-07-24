package com.example.fleettrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class locationData(
    @SerialName("driverID") val userId: String,
    @SerialName("password") val password: String,
    @SerialName("location") val location: Array<Double>
)