package com.dexter.kycsample.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIInterface {
    @POST("video-id-kyc/api/1.0/fetchKYCInfo")
    fun postKyc(@Body kycRequest: KYCRequest?): Call<KYCResponse?>?

    @POST("assisted/isUidaiUp")
    fun getStatus(): Call<UIDAIResponse?>?
}