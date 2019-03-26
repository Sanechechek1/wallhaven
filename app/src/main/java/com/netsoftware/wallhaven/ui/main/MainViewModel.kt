package com.netsoftware.wallhaven.ui.main

import androidx.lifecycle.MutableLiveData
import com.netsoftware.wallhaven.data.repositories.UserRepository
import com.netsoftware.wallhaven.utility.extensions.BaseViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(private var userRepository: UserRepository): BaseViewModel() {
    val newUserCounter =  MutableLiveData("No data.")
    val isLoading = MutableLiveData(false)

    fun refreshData(){
        isLoading.value = true
        userRepository.counter++
        userRepository.refreshData(object : UserRepository.OnDataReadyCallback {
            override fun onDataReady(data: String) {
                isLoading.value = false
                newUserCounter.value = data
            }
        })
    }
}
