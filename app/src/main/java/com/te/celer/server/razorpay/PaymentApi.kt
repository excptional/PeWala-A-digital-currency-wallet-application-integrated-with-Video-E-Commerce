package com.te.celer.server.razorpay

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("/api/payment/createOrder")
    fun createOrder(@Body orderRequest: List<Long>): Call<ArrayList<String>>
}