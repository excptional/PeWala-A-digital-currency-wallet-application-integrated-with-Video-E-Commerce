package com.te.celer.server.otp

import android.util.Log
import com.te.celer.BuildConfig
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OTPService {

    fun sendOtpMessage(phoneNumber: String, otp: String, callback: OTPCallback) {
        val message = "Hello User, Your OTP is $otp, Regards OnlineTestPanel"

        val call = Msg91ApiClient.apiService.sendOtp(
            authKey = BuildConfig.MSG91_AUTH_KEY,
            senderId = BuildConfig.MSG91_SENDER_ID,
            templateId = BuildConfig.MSG91_TEMPLATE_ID,
            route = BuildConfig.MSG91_ROUTE,
            mobileNumber = phoneNumber,
            message = message
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseBodyString = response.body()?.string()
                    Log.d("RT", "Raw response: $responseBodyString")
                    callback.onOtpSentSuccess("OTP sent successfully")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("RT", "Failed response: $errorMessage")
                    callback.onOtpSentFailure("Failed to send OTP. Response: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val errorMessage = t.message ?: "Unknown error"
                Log.e("RT", "Request failed: $errorMessage")
                callback.onOtpSentFailure("Failed to send OTP. Response: $errorMessage")
            }
        })

    }

    fun generateOtp(): String {
        return (1000..9999).random().toString()
    }

}