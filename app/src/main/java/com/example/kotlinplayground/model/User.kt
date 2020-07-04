package com.example.kotlinplayground.model

import com.google.gson.annotations.SerializedName;

data class NewArrivals(
    @SerializedName("name") val name: String,
    @SerializedName("model") val model: String
)
