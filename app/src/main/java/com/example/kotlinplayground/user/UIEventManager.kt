package com.example.kotlinplayground.user

interface UIEventManager {
    fun showToast(text: String)

    fun showProgressBar()

    fun hideProgressBar()
}