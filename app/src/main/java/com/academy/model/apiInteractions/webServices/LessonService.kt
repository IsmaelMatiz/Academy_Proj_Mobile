package com.academy.model.apiInteractions.webServices

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LessonService {
    @GET("Lessons")
    suspend fun getWeekLessons(
        @Query("careerId") careerId: Int,
        @Query("bimesterNum") bimesterNum: Int,
        @Query("weekNum") weekNum: Int
        ): Response<ResponseBody>
}