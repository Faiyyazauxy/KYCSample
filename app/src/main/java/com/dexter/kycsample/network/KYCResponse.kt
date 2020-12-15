package com.dexter.kycsample.network

import com.google.gson.annotations.SerializedName

data class KYCResponse(
        @SerializedName("response_status")
        val responseStatus: ResponseStatus,

        @SerializedName("response_data")
        val responseData: ResponseData
)

data class ResponseData(
        @SerializedName("is_encrypted")
        val encrypted: String,

        @SerializedName("kyc_info")
        val kycInfo: String,

        @SerializedName("hash")
        val hash: String
)

data class ResponseStatus(
        @SerializedName("status")
        val status: String,
        @SerializedName("code")
        val code: Long,
        @SerializedName("message")
        val message: String
)