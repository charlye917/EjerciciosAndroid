package com.charlye934.retrofitdemo.model

import com.google.gson.annotations.SerializedName


data class AlbumItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: Int
)