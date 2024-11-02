package com.te.pewala.server

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class TwoFactorApiClient {
    val BASE_URL = "http://2factor.in/API/V1/"

    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}