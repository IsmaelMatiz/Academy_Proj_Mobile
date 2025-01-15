package com.academy.modelViews

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.academy.Constants
import com.academy.model.apiInteractions.DTOs.Career
import com.academy.model.apiInteractions.DTOs.Lesson
import com.academy.model.apiInteractions.DTOs.Subject
import com.academy.model.apiInteractions.RetrofitClient
import com.academy.model.apiInteractions.webServices.LessonService
import com.academy.model.apiInteractions.webServices.StudentService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LessonVM(application: Application): AndroidViewModel(application) {
    private val lessonService = RetrofitClient.retrofit.create(LessonService::class.java)

    var lessonsInfo by mutableStateOf<List<Lesson>?>(null)

    var isLoading by mutableStateOf(false)
    var itWasSuccess by mutableStateOf(true)

    suspend fun getLessons(careerId:Int, bimesterNum: Int, weekNum: Int): Boolean{
        isLoading = true

        viewModelScope.async {
            val response = lessonService.getWeekLessons(careerId,bimesterNum,weekNum)

            if (response.isSuccessful)
            {
                Log.i(Constants.LOG_TAG.constVal, "Clases obtenidas exitosamente, empieza el" +
                        " mapeo")

                val responseBody = response.body()
                responseBody?.let {
                    val responseString = it.string()

                    val listType = object : TypeToken<List<Lesson>>() {}.type
                    val lessons: List<Lesson> = Gson().fromJson(responseString, listType)

                    lessonsInfo = lessons

                    itWasSuccess = true
                }
            }else{
                Log.e(Constants.LOG_TAG.constVal,
                    "La app regreso:\n" +
                            "status: ${response.raw().code}\n" +
                            "message: ${response.body()}")
                itWasSuccess = false
                lessonsInfo = null
            }

        }.await()
        isLoading = false
        return itWasSuccess
    }

    fun fetchLessons(careerId: Int, bimesterNum: Int, weekNum: Int) {
        viewModelScope.launch{
            getLessons(careerId, bimesterNum, weekNum)
        }
    }
}
