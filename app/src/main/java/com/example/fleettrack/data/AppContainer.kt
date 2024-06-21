package com.example.fleettrack.data

import com.example.fleettrack.network.FleetTrackApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val fleetTrackRepository: FleetTrackRepository
}

class DefaultAppContainer : AppContainer {
    private val BASE_URL = "https://fleet-track.vercel.app/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService: FleetTrackApiService by lazy {
        retrofit.create(FleetTrackApiService::class.java)
    }

    override val fleetTrackRepository: FleetTrackRepository by lazy {
        NetworkFleetTrackRepository(retrofitService)
    }
}