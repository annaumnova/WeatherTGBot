import DBSQL.DBSQLTG
import bot.WeatherBot
import data.remote.RetrofitClient
import data.remote.RetrofitType
import data.remote.repository.WeatherRepository

fun main() {
    val weatherRetrofit = RetrofitClient.getRetrofit(RetrofitType.WEATHER)
    val reverseRetrofit = RetrofitClient.getRetrofit(RetrofitType.REVERSE_GEOCODER)
    val weatherapi = RetrofitClient.getWeatherAPI(weatherRetrofit)
    val reversedapi = RetrofitClient.getReversedGeoCodingAPI(reverseRetrofit)
    val weatherRepository = WeatherRepository(weatherapi,reversedapi)

    val weatherbot = WeatherBot(weatherRepository).createBot()
    weatherbot.startPolling() //polling -- client always asks server

}