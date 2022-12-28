package data.remote.api

import data.remote.models.ReversedCountry
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

//to describe behavior
interface ReverseGeoCodingAPI {

    @GET("reverse") //endpoints
    fun getCountryNameByCoordinates(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("format") formatData: String,
    ) : Deferred<ReversedCountry>

}