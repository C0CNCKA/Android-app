package com.example.raspapp.api

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://dec.mgutm.ru/api/"

    val api: ApiService by lazy {
        Log.d("RetrofitInstance", "Создаём Retrofit с GsonConverterFactory")
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        Log.d("RetrofitInstance", "Retrofit создан, создаём ApiService")
        retrofit.create(ApiService::class.java)
    }
}