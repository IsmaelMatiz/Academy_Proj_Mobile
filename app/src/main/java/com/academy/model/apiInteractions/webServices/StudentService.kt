package com.academy.model.apiInteractions.webServices

import com.academy.model.apiInteractions.DTOs.Student
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface StudentService {
    @POST("Student")
    suspend fun createStudent(@Body student: Student): Response<String>

    @GET("Student/{email}")
    suspend fun getStudentByEmail(@Path("email") email: String): Response<ResponseBody>

    @PUT("Student/{id}")
    suspend fun updateStudent(@Path("id") id: Int, @Body student: Student): Response<ResponseBody>

    @DELETE("Student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int): Response<ResponseBody>
}