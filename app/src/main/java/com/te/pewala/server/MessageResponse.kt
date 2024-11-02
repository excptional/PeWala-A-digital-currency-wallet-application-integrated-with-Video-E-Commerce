package com.te.pewala.server

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class MessageResponse {
    @SerializedName("Status")
    @Expose
    private var status: String? = null

    @SerializedName("Details")
    @Expose
    private var details: String? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getDetails(): String? {
        return details
    }

    fun setDetails(details: String?) {
        this.details = details
    }
}
