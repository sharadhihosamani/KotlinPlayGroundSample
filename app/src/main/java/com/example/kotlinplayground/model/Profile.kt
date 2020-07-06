package com.example.kotlinplayground.model

import com.google.gson.annotations.SerializedName;

data class Profile(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("mobile") val phoneNumber: String
)
