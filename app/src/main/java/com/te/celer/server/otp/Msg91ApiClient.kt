package com.te.celer.server.otp

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Msg91ApiClient {
    private const val BASE_URL = "https://api.msg91.com/api/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: Msg91ApiService by lazy {
        retrofit.create(Msg91ApiService::class.java)
    }
}
