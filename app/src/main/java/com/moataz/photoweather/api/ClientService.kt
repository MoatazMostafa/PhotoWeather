package com.moataz.photoweather.api

import com.moataz.photoweather.models.CurrentWeather
import com.moataz.photoweather.utils.Constants.API_KEY
import com.moataz.photoweather.utils.Constants.BASE_URL
import com.moataz.photoweather.utils.Constants.UNIT
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ClientService {
    @GET("data/2.5/weather")
   suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") unit: String = UNIT,
    ): Response<CurrentWeather>
}

object ClientNetwork {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(HttpClient.getClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val clientService: ClientService = retrofit.create(ClientService::class.java)
}