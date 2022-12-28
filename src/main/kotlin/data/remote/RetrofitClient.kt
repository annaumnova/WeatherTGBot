package data.remote
//for working API server
//(https://developer.alexanderklimov.ru/android/library/retrofit.php)

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import data.remote.api.ReverseGeoCodingAPI
import data.remote.api.WeatherAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val WEATHER_BASE_URL = "http://api.weatherapi.com/v1/" //base url ended by slash
private const val REVERSE_GEOCODER_BASE_URL =
    "https://nominatim.openstreetmap.org/"//latitude + longitude = name of geo object
const val API_KEY = "4e9971c16bb546978e6230028222712"

//enum class for
enum class RetrofitType(val baseUrl: String) {
    WEATHER(WEATHER_BASE_URL),
    REVERSE_GEOCODER(REVERSE_GEOCODER_BASE_URL)
}

//that object is used for
object RetrofitClient {

    //
    fun getRetrofit(retrofitType: RetrofitType): Retrofit {
        return Retrofit.Builder()
            .baseUrl(retrofitType.baseUrl)
            .client(getClient())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //
    fun getClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(logging)
        return okHttpClient.build()
    }


    //
    fun getWeatherAPI(retrofit: Retrofit): WeatherAPI {
        return retrofit.create(WeatherAPI::class.java)
    }

    //
    fun getReversedGeoCodingAPI(retrofit: Retrofit): ReverseGeoCodingAPI {
        return retrofit.create(ReverseGeoCodingAPI::class.java)
    }

}