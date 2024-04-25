package com.example.voiceassistent.cityinformation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class CityService {
    fun getApi() : CityApi? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://htmlweb.ru")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
        return retrofit.create(CityApi::class.java)
    }
}