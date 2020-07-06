package com.example.kotlinplayground.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

    class ProfileViewModelFactory(private val eventManager: UIEventManager) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                eventManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}