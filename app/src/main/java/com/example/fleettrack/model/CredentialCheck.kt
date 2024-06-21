package com.example.fleettrack.model

import kotlinx.serialization.Serializable

@Serializable
data class CredentialCheck(
    val isValid: Boolean
)