package com.example.kotlinplayground.retrofit

import com.example.kotlinplayground.model.NewArrivals
import com.example.kotlinplayground.model.Profile
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("getnewmodels")
    suspend fun getList(): List<NewArrivals>

    @GET("getprofile")
    suspend fun getProfile() : Profile
}