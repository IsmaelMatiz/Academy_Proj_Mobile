package com.academy.model.apiInteractions.DTOs

data class ProgressStudent(
    val id: Int,
    val student: Student,
    val status: String,
    val currentBimester: Int,
    val currentWeek: Int,
    val currentClass: Int
)
