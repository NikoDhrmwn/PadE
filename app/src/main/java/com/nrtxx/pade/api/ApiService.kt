package com.nrtxx.pade.api

import com.nrtxx.pade.helper.PenyakitResponse
import com.nrtxx.pade.helper.WeatherItem
import com.nrtxx.pade.helper.WeatherResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("penyakit/{namaPenyakit}")
    fun getPenyakit(
        @Path("namaPenyakit") namaPenyakit: String
    ): Call<PenyakitResponse>

    @GET("weather")
    fun getWeather(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("appid") appid: String
    ): Call<WeatherResponse>

    @GET("weather")
    fun getWeather2(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("appid") appid: String
    ): Call<WeatherItem>
}