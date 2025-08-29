package com.example.raspapp.api

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.raspapp.data.ScheduleResponse

interface ApiService {
    @GET("Rasp")
    suspend fun getSchedule(
        @Query("idGroup") idGroup: Int,
        @Query("sdate") sdate: String
    ): ScheduleResponse
}