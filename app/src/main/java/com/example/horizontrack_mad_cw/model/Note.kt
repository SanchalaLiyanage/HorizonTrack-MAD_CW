package com.example.horizontrack_mad_cw.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    var id: String = "",
    val title: String,
    val content: String,
    val imageUri: String? = null
) : Parcelable
