package com.example.horizontrack_mad_cw

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val title: String,
    val content: String,
    val imageUri: String?
) : Parcelable
