package com.example.calendarapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.UnknownHostException
import java.security.cert.X509Certificate
import javax.net.ssl.*

object CalendRuParser {

    private val unsafeClient = createUnsafeClient()

    private fun createUnsafeClient(): SSLContext {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        return sslContext
    }

    // Ключевые слова которые точно есть в названиях праздников
    private val holidayKeywords = listOf(
        "день", "День", "праздник", "Праздник", "международный", "Международный",
        "всемирный", "Всемирный", "европейский", "Европейский", "народный", "Народный"
    )

    // Мусорные слова которые надо игнорировать
    private val garbageWords = listOf(
        "Марк", "Ключник", "Запрягальник", "Пролетье", "Купальница",
        "Вешний", "Ветхопещерник", "Огородник", "Евсеев", "Проклов",
        "Егорий", "Еремей", "Иван", "Кузьма", "Лука", "Федор", "Федора"
    )

    suspend fun getHolidaysForDay(month: Int, day: Int): List<Holiday> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://www.dacha6.ru/calendar/$month/$day/"

                val doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .sslSocketFactory(unsafeClient.socketFactory)
                    .get()

                val fullText = doc.body().text()
                val holidays = mutableListOf<Holiday>()

                val startIndex = fullText.indexOf("Нового года")
                val endIndex = fullText.indexOf("Церковный календарь")

                if (startIndex >= 0 && endIndex > startIndex) {
                    val afterNewYear = fullText.substring(startIndex)
                    val dnIndex = afterNewYear.indexOf("дн.")

                    if (dnIndex >= 0) {
                        val afterDays = afterNewYear.substring(dnIndex + 3).trim()
                        val churchIndex = afterDays.indexOf("Церковный календарь")
                        val holidaysSection = afterDays.substring(0, churchIndex).trim()

                        // Разбиваем по заглавным буквам
                        val regex = Regex("(?=[А-ЯA-Z])")
                        val items = holidaysSection
                            .split(regex)
                            .map { it.trim() }
                            .filter { it.isNotEmpty() && it.length > 2 }

                        // Склеиваем обрывки и фильтруем
                        val result = mutableListOf<String>()
                        var current = ""

                        for (item in items) {
                            if (item in garbageWords) continue

                            val isNewHoliday = holidayKeywords.any { kw ->
                                item.contains(kw, ignoreCase = true)
                            }

                            if (isNewHoliday) {
                                if (current.isNotEmpty() && current.length > 5) {
                                    result.add(current)
                                }
                                current = item
                            } else if (current.isNotEmpty()) {
                                current += " $item"
                            }
                        }

                        if (current.isNotEmpty() && current.length > 5) {
                            result.add(current)
                        }

                        for (name in result) {
                            if (name.length in 6..200 &&
                                !garbageWords.any { name.equals(it, ignoreCase = true) }) {
                                holidays.add(
                                    Holiday(
                                        month = month,
                                        day = day,
                                        name = name,
                                        emoji = "📅",
                                        description = ""
                                    )
                                )
                            }
                        }
                    }
                }

                holidays
            } catch (e: UnknownHostException) {
                throw e
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}