package data.remote.api

import data.remote.models.CurrentWeather
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") apiKey:String,
        @Query("query") countryName:String,
        @Query("aqi") airQualityData:String
    ): Deferred<CurrentWeather>
}