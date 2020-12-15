package com.dexter.kycsample.network

import com.google.gson.annotations.SerializedName

data class UIDAIResponse(
        @SerializedName("status")
        val status: String,
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String
)