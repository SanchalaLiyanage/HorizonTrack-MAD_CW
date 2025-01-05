package com.example.horizontrack_mad_cw.model

data class User(
    val name: String,
    val email: String,
) {
    constructor() : this("", "")
}