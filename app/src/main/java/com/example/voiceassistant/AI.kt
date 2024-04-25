package com.example.voiceassistent

import ParsingHtmlService
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.voiceassistent.cityinformation.CityToString
import com.example.voiceassistent.forecast.ForecastToString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.regex.Matcher
import java.util.regex.Pattern

class AI {
    enum class timeAnswer {
        day,
        hour,
        dayOfWeek,
        timeToDay
    }

    val answers = mapOf(
        "Привет" to "Симметрично",
        "Чем занимаешься" to "Отвечаю на вопросы",
        "Как дела" to "Неплохо",
        "Какой сегодня день" to getTimeAnswer(timeAnswer.day, "null"),
        "Который час" to getTimeAnswer(timeAnswer.hour, "null"),
        "Какой день недели" to getTimeAnswer(timeAnswer.dayOfWeek, "null"),
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    fun getAnswer(text: String, answerCallBack: (String) -> Unit) {
        var result: MutableList<String> = ArrayList()

        val weatherPattern: Pattern =
            Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
        var matcher: Matcher = weatherPattern.matcher(text)
        if (matcher.find()) {
            val cityName: String = matcher.group(1)
            ForecastToString().getForecast(cityName) {
                result.add(it)
                answerCallBack.invoke(result.last())
            }
            result.add("Я не знаю какая погода в городе $cityName")
        } else {

        }

        val cityPattern: Pattern =
            Pattern.compile("расскажи о городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
        matcher = cityPattern.matcher(text)
        if (matcher.find()) {
            val cityName: String = matcher.group(1)
            CityToString().getCityInformaion(cityName) {
                if (it != null && it.count() > 5) {
                    answerCallBack.invoke("Найдена информация о нескольких городах")
                    for (x in 0..5) {
                        result.add(it[x])
                        answerCallBack.invoke(result.last())
                    }
                } else if (it != null && it.count() > 1) {
                    answerCallBack.invoke("Найдена информация о нескольких городах")
                    for (x in 0..it.size) {
                        result.add(it[x])
                        answerCallBack.invoke(result.last())
                    }
                } else if (it != null) {
                    answerCallBack.invoke("Найдена информация о городе")
                    result.add(it[0])
                    answerCallBack.invoke(result.last())
                } else {
                    result.add("Информация о городе $cityName не найдена")
                    answerCallBack.invoke(result.last())
                }
            }
        }

        for ((x, y) in answers) {
            if (text.contains("сколько дней до", ignoreCase = true)) {
                result.add(
                    getTimeAnswer(
                        timeAnswer.timeToDay,
                        text.substring(text.lastIndexOf(" ") + 1)
                    )
                )
            } else if (text.contains(x, ignoreCase = true)) {
                result.add(y)
                answerCallBack.invoke(result.last())
            }
        }

        if (text.contains("какой праздник", ignoreCase = true)) {
            Observable.fromCallable {
                val answer = ParsingHtmlService().getHoliday(text)
                result.add(answer)
                return@fromCallable result
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    answerCallBack.invoke(result.last())
                }


        } else if (result.isEmpty()) {
            result.add("Вопрос понял. Думаю...")
            answerCallBack.invoke(result.last())
        }


    }

    private fun getTimeAnswer(type: Enum<timeAnswer>, date: String): String {
        when (type) {
            timeAnswer.day -> {
                return android.text.format.DateFormat.format("dd.MM.yyyy", Calendar.getInstance())
                    .toString()
            }

            timeAnswer.hour -> {
                return android.text.format.DateFormat.format("HH:mm", Calendar.getInstance())
                    .toString()
            }

            timeAnswer.dayOfWeek -> {
                return android.text.format.DateFormat.format("EEEE", Calendar.getInstance())
                    .toString()
            }

            timeAnswer.timeToDay -> {
                val targetDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(date)
                if (targetDate != null) {
                    val currentDate = Date()
                    val differenceInMillis = targetDate!!.time - currentDate.time
                    val daysDifference = differenceInMillis / (24 * 60 * 60 * 1000)

                    return if (daysDifference >= 0) {
                        "До ${
                            SimpleDateFormat(
                                "dd.MM.yyyy",
                                Locale.getDefault()
                            ).format(targetDate!!)
                        } осталось $daysDifference дней"
                    } else {
                        "Уже прошло"
                    }
                } else {
                    return "Вы не указали конечную дату. Пожалуйста, уточните дату в формате dd.MM.yyyy."
                }
            }

            else -> {
                return "Вопрос понял. Думаю..."
            }
        }
    }
}