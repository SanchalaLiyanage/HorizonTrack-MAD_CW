package com.example.horizontrack_mad_cw.model

data class UserBMI(
    val gender: String = "",
    val height: Float = 0f,
    val weight: Float = 0f,
    val age: Int = 0,
    val bmi: Float = 0f,
    val userId: String = ""
)
