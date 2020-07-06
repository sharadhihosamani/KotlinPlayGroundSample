package com.example.kotlinplayground.repository

import com.example.kotlinplayground.model.NewArrivals
import com.example.kotlinplayground.model.Profile
import com.example.kotlinplayground.retrofit.ApiService
import com.example.kotlinplayground.retrofit.RetrofitInstance

class AppRepository {
    private var service: ApiService = RetrofitInstance.appService
    suspend fun getList(): List<NewArrivals> = service.getList()
    suspend fun getProfile(): Profile = service.getProfile()
}