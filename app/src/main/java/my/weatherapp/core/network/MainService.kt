package my.weatherapp.core.network

import my.weatherapp.core.model.current.CurrentWeather
import my.weatherapp.core.model.daily.DailyData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MainService {

    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String
    ): Call<CurrentWeather>

    @GET("onecall")
    fun getDailyWeekly(
        @Query("lat") lat: String, @Query("lon") lon: String,
        @Query("exlude") exlude: String,
        @Query("appid") appid: String
    ): Call<DailyData>


}