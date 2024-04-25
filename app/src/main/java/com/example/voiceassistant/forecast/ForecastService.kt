package com.example.voiceassistent.forecast

import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class ForecastService {
    fun getApi(): ForecastApi? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            //.addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
        return retrofit.create(ForecastApi::class.java)
    }
}