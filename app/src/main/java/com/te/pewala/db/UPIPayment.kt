package com.te.pewala.db

data class UPIPayment(
    val vpa: String,
    val name: String,
    val description: String,
    val transactionId: String,
    val amount: String
)