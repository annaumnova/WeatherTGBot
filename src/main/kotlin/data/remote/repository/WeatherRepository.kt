package data.remote.repository

import data.remote.api.ReverseGeoCodingAPI
import data.remote.api.WeatherAPI
import data.remote.models.CurrentWeather
import data.remote.models.ReversedCountry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//useful class for maneging your queries
//it helps to their get together and ect.
//it solves what kind of data is needed (local or not)
class WeatherRepository(private val weatherAPI: WeatherAPI, private val reverseGeoCodingAPI: ReverseGeoCodingAPI) {

    //for getting currrent ("weather api")
    suspend fun getCurrentWeather(apiKey: String, countryName: String, airQualityData: String): CurrentWeather {
        return withContext(Dispatchers.IO) {//for switching streams, IO - input/output
            weatherAPI.getCurrentWeather(apiKey, countryName, airQualityData)
        }.await()// to wait until to get the result
    }

    //for reverse geo coding latitude and longtitude ("open street map")
    suspend fun getCountryName(latitude: String, longitude: String, format: String): ReversedCountry {
        return withContext(Dispatchers.IO) { //for switching streams, IO - input/output
            reverseGeoCodingAPI.getCountryNameByCoordinates(latitude, longitude, format)
        }.await() // to wait until to get the result
    }
}