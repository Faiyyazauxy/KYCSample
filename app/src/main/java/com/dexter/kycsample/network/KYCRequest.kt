package com.dexter.kycsample.network

import com.google.gson.annotations.SerializedName

data class KYCRequest(
        val headers: Headers,
        val request: Request
)

data class Headers(
        @SerializedName("client_code")
        val clientCode: String,

        @SerializedName("sub_client_code")
        val subClientCode: String,

        @SerializedName("actor_type")
        val actorType : String,

        @SerializedName("channel_code")
        val channelCode : String,

        @SerializedName("stan")
        val stan: String,

        @SerializedName("user_handle_type")
        val userHandleType : String,

        @SerializedName("user_handle_value")
        val userHandleValue : String,

        @SerializedName("location")
        val location : String,

        @SerializedName("transmission_datetime")
        val transmissionDate : String,

        @SerializedName("run_mode")
        val runMode: String,

        @SerializedName("client_ip")
        val clientIp: String,

        @SerializedName("operation_mode")
        val operationMode: String,

        @SerializedName("channel_version")
        val channelVersion: String,

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