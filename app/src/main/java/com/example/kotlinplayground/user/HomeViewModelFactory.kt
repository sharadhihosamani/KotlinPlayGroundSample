package com.example.kotlinplayground.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModelFactory(private val eventManager: UIEventManager) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                eventManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}