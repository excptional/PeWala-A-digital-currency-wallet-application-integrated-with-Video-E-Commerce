package com.te.pewala.server.razorpay

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RazorpayOrderApiClient {
//    private val BASE_URL = "https://razorpay-order-app.onrender.com" // Replace with your server URL
    private val BASE_URL = "http://192.168.139.92:8080/" // Replace with your server URL

    private val unsafeOkHttpClient: OkHttpClient by lazy {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true } // Trust all hostnames
            .build()
    }

    // Retrofit instance with the unsafe OkHttpClient (for development)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(unsafeOkHttpClient) // Use the unsafe client to bypass SSL verification
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Payment API interface
    val paymentApi: PaymentApi by lazy {
        retrofit.create(PaymentApi::class.java)
    }

}