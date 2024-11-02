package com.te.pewala.server.otp

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Msg91ApiService {
    @GET("sendhttp.php")
    fun sendOtp(
        @Query("authkey") authKey: String,
        @Query("sender") senderId: String,
        @Query("DLT_TE_ID") templateId: String,
        @Query("route") route: String,
        @Query("mobiles") mobileNumber: String,
        @Query("country") country: String = "91",
        @Query("message") message: String
    ): Call<ResponseBody>
}
