package com.te.celer.server.otp

interface OTPCallback {
    fun onOtpSentSuccess(message: String)
    fun onOtpSentFailure(error: String)
}