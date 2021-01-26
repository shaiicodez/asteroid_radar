package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.NetworkPictureOfDay
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

enum class NasaApiStatus {LOADING, ERROR, DONE}

private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS).build()
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()


// API INTERFACE
interface NasaApiService {

    @GET("neo/rest/v1/feed/")
    suspend fun getAsteroidList(
        @Query("start_date") startDate: String,
        @Query("end_date")   endDate: String,
        @Query("api_key")    apiKey: String): String

    // send requests to NASA servers to get the POD (picture of the day)
    // use the NeoWs URL path after the BASE URL
    @GET("planetary/apod")
    suspend fun getPictureOfDay(
        @Query("api_key")   apiKey: String): NetworkPictureOfDay
} // NasaApiService

object NasaApi {

    val retrofitService: NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
} // end Network object