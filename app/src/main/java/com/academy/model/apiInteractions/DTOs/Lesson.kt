package com.academy.model.apiInteractions.DTOs

data class Lesson(
    val lessonId: Int,
    val subject: Subject,
    val career: Career,
    val numBimester: Int,
    val numWeek: Int,
    val posWeek: Int,
    val lessonTitle: String,
    val teacherName: String,
    val contentDescrip: String,
    val linkToContent: String,
    val contentType: String
)
