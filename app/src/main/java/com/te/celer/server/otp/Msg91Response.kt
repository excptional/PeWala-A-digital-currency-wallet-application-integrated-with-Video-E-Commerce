package com.te.celer.server.otp

data class Msg91Response(
    val type: String,
    val message: String,
    val details: Details
)

data class Details(
    val otp_id: String
)