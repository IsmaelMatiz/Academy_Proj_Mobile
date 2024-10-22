package com.academy.model.apiInteractions.DTOs

data class Career(
    val careerId: Int = 0,
    val careerName: String = "",
    val numBimesters: Int = 0,
    val subjects: List<Subject> = emptyList()
)
