package com.archit.meetingscheduler.networkUtils.networkClient

import com.archit.meetingscheduler.activity.MainActivity
import com.archit.meetingscheduler.networkUtils.NetworkUtils
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object RetrofitClient {

    /**
     * returns cache enables Retrofit client
     * maintains 10 seconds cache while online
     * maintains 7 days cache when offline
     */
    fun getCacheEnabledRetrofit(): Retrofit {

        val onlineCacheInterceptor = Interceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            val cacheControl = originalResponse.header("Cache-Control")
            if (cacheControl == null || cacheControl!!.contains("no-store") || cacheControl!!.contains("no-cache") ||
                cacheControl!!.contains("must-revalidate") || cacheControl!!.contains("max-age=0")
            ) {
                originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + 10)
                    .build()
            } else {
                originalResponse
            }
        }

        val offlineCacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtils.isConnected) {
                val maxStale = 60 * 60 * 24 * 7
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .build()
            }
            chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(onlineCacheInterceptor)
            .addInterceptor(offlineCacheInterceptor)
            .cache(Cache(File(MainActivity.mInstance.cacheDir, "http-cache"), 100 * 1024 * 1024))
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl("https://fathomless-shelf-5846.herokuapp.com/")
            .build()
    }
}

