package com.academy

enum class UserTypes(){
    STUDENT,
    TEACHER
}

enum class Constants(val constVal: String){
    BASE_URL("http://146.235.33.173:8080/api/"),
    LOG_TAG("AcademyDebug")
}