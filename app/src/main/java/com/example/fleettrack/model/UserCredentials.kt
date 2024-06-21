package com.example.fleettrack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(
    @SerialName("user_id")
    val userid: String,
    val password: String
)