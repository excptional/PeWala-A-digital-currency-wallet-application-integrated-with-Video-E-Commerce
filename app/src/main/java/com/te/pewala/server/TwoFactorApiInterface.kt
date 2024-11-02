package com.te.pewala.server

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TwoFactorApiInterface {
    @GET("{api_key}/SMS/+91{users_phone_no}/AUTOGEN3")
    fun sentOTP(
        @Path("api_key") apiKey: String?,
        @Path("users_phone_no") phone_no: String?
    ): Call<MessageResponse?>?

    @GET("{api_key}/SMS/VERIFY/{session_id}/{otp_entered_by_user}")
    fun verifyOTP(
        @Path("api_key") apiKey: String?,
        @Path("session_id") session_id: String?,
        @Path("otp_entered_by_user") otp_entered_by_user: String?
    ): Call<MessageResponse?>?
}