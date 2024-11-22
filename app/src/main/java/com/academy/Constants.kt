package com.academy

enum class UserTypes(){
    STUDENT,
    TEACHER
}

enum class Constants(val constVal: String){
    BASE_URL("http://192.168.1.5:8080/api/"),
    LOG_TAG("AcademyDebug")
}