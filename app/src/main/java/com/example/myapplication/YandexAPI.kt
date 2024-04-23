package com.example.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexAPI {
    @GET("/api/v1/predict.json/complete")
    fun complete(
        @Query("key") key345345: String,
        @Query("q") q222: String,
        @Query("lang") lang: String,
        @Query("limit") limit: Int
    ) : Call<Answer>
}