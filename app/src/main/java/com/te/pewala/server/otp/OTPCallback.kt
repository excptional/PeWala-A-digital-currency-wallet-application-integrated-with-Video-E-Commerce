package com.te.pewala.server.otp

interface OTPCallback {
    fun onOtpSentSuccess(message: String)
    fun onOtpSentFailure(error: String)
}