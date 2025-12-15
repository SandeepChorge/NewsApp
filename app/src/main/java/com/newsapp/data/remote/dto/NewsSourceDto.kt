package com.newsapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsSourceDto(
    @SerializedName("id")
    val id: String?, // 'id' can be null in the JSON

    @SerializedName("name")
    val name: String
)