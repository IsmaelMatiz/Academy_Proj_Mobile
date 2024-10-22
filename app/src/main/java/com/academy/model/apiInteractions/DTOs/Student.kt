package com.academy.model.apiInteractions.DTOs

data class Student(
    val id: Int?,
    val fullName: String,
    val email: String,
    val profilePic: String,
    val userType: String,
    val choosenCareer: Career
)
