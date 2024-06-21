package com.example.fleettrack.data

import com.example.fleettrack.model.CredentialCheck
import com.example.fleettrack.model.Trip
import com.example.fleettrack.network.FleetTrackApiService
import retrofit2.Response

interface FleetTrackRepository {
    suspend fun getTrips(userId: String, password: String): List<Trip>
    suspend fun checkCredentials(userId: String, password: String): CredentialCheck
}

class NetworkFleetTrackRepository(
    private val fleetTrackApiService: FleetTrackApiService
): FleetTrackRepository {
    override suspend fun getTrips(userId: String, password: String): List<Trip> = fleetTrackApiService.getTrips(userId, password)
    override suspend fun checkCredentials(userId: String, password: String): CredentialCheck = fleetTrackApiService.checkCredentials(userId, password)
}