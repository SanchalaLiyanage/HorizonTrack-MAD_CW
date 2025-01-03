package com.example.horizontrack_mad_cw.models

data class User(
    val name: String,
    val email: String,
) {
    constructor() : this("", "")
}