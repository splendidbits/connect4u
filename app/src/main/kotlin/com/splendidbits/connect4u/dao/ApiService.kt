package com.splendidbits.connect4u.dao

import com.google.gson.JsonArray
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("production")
    fun submitFetchMoves(@Query("moves") currentMoves: String): Observable<JsonArray>

    /**
     * Companion object to create the 9DT Service
     */
    companion object Factory {
        fun create(baseUrl: String): ApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}