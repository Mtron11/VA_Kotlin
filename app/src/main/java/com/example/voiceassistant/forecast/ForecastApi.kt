package com.example.voiceassistent.forecast

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {
    @GET("/data/2.5/weather?appid=ae037f8d311d56ab041f752ade967f26&lang=ru&units=metric&mode=xml")
    fun getCurrentWeather(@Query("q") city: String?) : Call<Forecast?>?
}