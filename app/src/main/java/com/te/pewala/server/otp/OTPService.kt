package com.te.pewala.server.otp

import android.util.Log
import com.google.gson.Gson
import com.te.pewala.BuildConfig
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
                if (response.isSuccessful) {
                    try {
                        val responseBodyString = response.body()?.string()
                        Log.d("RT", "Raw response: $responseBodyString")
                        callback.onOtpSentSuccess("OTP sent successfully")

//                        Check if response is JSON
//                        if (responseBodyString.startsWith("{")) {
//                            // Parse as JSON
//                            val gson = Gson()
//                            val msg91Response = gson.fromJson(responseBodyString, Msg91Response::class.java)
//                            callback.onOtpSentSuccess(msg91Response.message ?: "OTP sent successfully")
//                        } else {
//                            // Handle as plain text
//                            callback.onOtpSentSuccess(responseBodyString)
//                        }

                    } catch (e: Exception) {
                        Log.e("RT", "Error parsing response: ${e.message}")
                        callback.onOtpSentFailure("Failed to send OTP. Response: ${e.message}")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("RT", "Failed response: $errorMessage")
                    callback.onOtpSentFailure("Failed to send OTP. Response: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val errorMessage = t.message ?: "Unknown error"
                Log.e("RT", "Request failed: $errorMessage")
                callback.onOtpSentFailure("Error: $errorMessage")
            }
        })


    }

    fun generateOtp(): String {
        return (1000..9999).random().toString()
    }

}