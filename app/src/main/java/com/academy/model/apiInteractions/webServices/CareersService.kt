package com.academy.model.apiInteractions.webServices


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface CareersService {
    @GET("Careers")
    fun getAllCareers():Call<ResponseBody>
}