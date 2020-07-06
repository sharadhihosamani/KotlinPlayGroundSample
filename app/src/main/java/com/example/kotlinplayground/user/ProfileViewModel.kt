package com.example.kotlinplayground.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.kotlinplayground.model.Profile
import com.example.kotlinplayground.repository.AppRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class ProfileViewModel(private val eventManager: UIEventManager) : ViewModel() {

    private val repository = AppRepository()
    lateinit var profileDetails: Profile
    fun loadDataFromWeb() = liveData {
        try {
            delay(1000)
            profileDetails = repository.getProfile()
            emit(profileDetails)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
