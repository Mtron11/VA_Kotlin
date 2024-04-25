package com.example.voiceassistent.cityinformation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {
    @GET("xml/geo/search?api_key=f23830a856cc933655189b07cdc938e1")
    fun getCurrentCity(@Query("search") city: String?) : Call<CityInformation?>?
}