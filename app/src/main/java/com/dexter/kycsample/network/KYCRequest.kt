package com.dexter.kycsample.network

import com.google.gson.annotations.SerializedName

data class KYCRequest(
        val headers: Headers,
        val request: Request
)

data class Headers(
        @SerializedName("client_code")
        val clientCode: String,

        @SerializedName("stan")
        val stan: String,

        @SerializedName("run_mode")
        val runMode: String,

        @SerializedName("function_code")
        val functionCode: String,

        @SerializedName("function_sub_code")
        val functionSubCode: String
)

data class Request(
        @SerializedName("api_key")
        val apiKey: String,

        @SerializedName("user_id")
        val userID: String,

        @SerializedName("hash")
        val hash: String
)