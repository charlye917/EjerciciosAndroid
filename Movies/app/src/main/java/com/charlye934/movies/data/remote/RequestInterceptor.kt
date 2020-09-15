package com.charlye934.movies.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttUrl = originalRequest.url()

        val url = originalHttUrl.newBuilder()
            .addQueryParameter("api_key", ApiConstants.API_KEY)
            .build()

        val request = originalRequest.newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}