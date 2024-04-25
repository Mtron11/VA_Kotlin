package com.example.voiceassistent.forecast

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForecastToString {
    fun getForecast(city: String?, callback: (String) -> Unit) {
        val api: ForecastApi? = ForecastService().getApi()
        val call: Call<Forecast?>? = api?.getCurrentWeather(city)

        call!!.enqueue(object : Callback<Forecast?> {
            override fun onResponse(call: Call<Forecast?>, response: Response<Forecast?>) {
                val result = response.body()
                if (result != null) {
                    val answer =
                        "Сейчас где-то ${result.temperature?.value} ${getCorrectString(result.temperature?.value)} и ${result.weather?.value}"
                    callback.invoke(answer)
                } else {
                    callback.invoke("Не могу узнать погоду")
                }
            }

            override fun onFailure(call: Call<Forecast?>, t: Throwable) {
                Log.w("WEATHER", t.message.toString())
            }
        })
    }

    fun getCorrectString(temp: String?): String {
        if (temp != null) {
            return if (temp.contains(".")) {
                "градуса"
            } else {
                val lastNum = temp[temp.lastIndex].digitToInt()
                when (lastNum) {
                    1 -> {
                        "градус"
                    }
                    in 2..4 -> {
                        "градуса"
                    }
                    else -> {
                        "градусов"
                    }
                }
            }
        }
        else return "градусов"
    }
}