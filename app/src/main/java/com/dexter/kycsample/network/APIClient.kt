package com.dexter.kycsample.network;

import android.content.Context
import com.dexter.kycsample.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Modifier
import java.util.concurrent.TimeUnit

object APIClient {
    private var retrofit: Retrofit? = null

    /**
     * @param context context of the class
     * @return retrofit instance
     */
    fun getClient(context: Context?, url : String): Retrofit? {
        if (retrofit == null) {
            val httpClient = OkHttpClient.Builder()
            httpClient.connectTimeout(120, TimeUnit.SECONDS)
            httpClient.readTimeout(120, TimeUnit.SECONDS)
            httpClient.writeTimeout(120, TimeUnit.SECONDS)
            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                //print the logs in this case
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            } else {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            }
            httpClient.addInterceptor(loggingInterceptor)
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request: Request
                request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .method(original.method(), original.body())
                        .build()
                chain.proceed(request)
            }
            val client = httpClient.build()
            val gson = GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .setLenient()
                    .create()
            retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
        }
        return retrofit
    }
}