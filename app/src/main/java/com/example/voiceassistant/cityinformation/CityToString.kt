package com.example.voiceassistent.cityinformation

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CityToString {
    fun getCityInformaion(city: String?, callback: (MutableList<String>?) -> Unit) {
        val api: CityApi? = CityService().getApi()
        val call: Call<CityInformation?>? = api?.getCurrentCity(city)

        call!!.enqueue(object : Callback<CityInformation?> {
            override fun onResponse(call: Call<CityInformation?>, response: Response<CityInformation?>) {
                val result = response.body()
                if (result != null) {
                    val answer: MutableList<String> = ArrayList()
                    for (city in result.city?.cityMsgs!!) {
                        answer.add(
                            "Информация о городе ${city.name}\n" +
                                    "Полное название: ${city.fullName}\n" +
                                    "Страна: ${getCountry(city.country)}\n" +
                                    "Больше информации на сайте: ${city.url}"
                        )
                    }
                    callback.invoke(answer)
                } else {
                    callback.invoke(null)
                }
            }

            override fun onFailure(call: Call<CityInformation?>, t: Throwable) {
                Log.w("CITY", t.message.toString())
            }
        })
    }

    fun getCountry(country: String?): String {
        return if (country != null) {
            when (country) {
                "RU" -> "Россия"
                "UK" -> "Великобритания"
                "US" -> "США"
                else -> "Неизвестно"
            }
        } else {
            "Неизвестно"
        }
    }
}