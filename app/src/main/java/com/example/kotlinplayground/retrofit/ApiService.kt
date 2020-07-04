package com.example.kotlinplayground.retrofit

import com.example.kotlinplayground.model.NewArrivals
import retrofit2.http.GET

interface ApiService {
    @GET("getnewmodels")
    suspend fun getList(): List<NewArrivals>
}