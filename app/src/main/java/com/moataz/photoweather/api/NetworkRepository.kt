package com.moataz.photoweather.api

import com.moataz.photoweather.models.CurrentWeather

class NetworkRepository {

    suspend fun getCurrentWeather(lat: Double, lon: Double, onFinish: (CurrentWeather?, String) -> Unit) {
        val response = ClientNetwork.clientService.getCurrentWeather(lat, lon)
        if (response.isSuccessful)
            onFinish.invoke(response.body(), "")
        else {
            onFinish.invoke(null, response.message())
        }
    }

}