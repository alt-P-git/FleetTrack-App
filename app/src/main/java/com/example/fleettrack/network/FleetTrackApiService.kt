package com.example.fleettrack.network

import com.example.fleettrack.model.CredentialCheck
import com.example.fleettrack.model.Trip
import com.example.fleettrack.model.locationData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FleetTrackApiService {
    @GET("getDriverTrips")
    suspend fun getTrips(
        @Query("driverID") userId: String,
        @Query("password") password: String
    ): List<Trip>

    @GET("checkDriver")
    suspend fun checkCredentials(
        @Query("driverID") userId: String,
        @Query("password") password: String
    ): CredentialCheck

    @POST("updateDriverLocation")
    suspend fun sendLocation(
        @Body locationData: locationData
    ): Response<Unit>
}