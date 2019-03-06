package com.netsoftware.wallhaven.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.netsoftware.wallhaven.data.UserModel

class MainViewModel : ViewModel() {
    private val userModel = UserModel()

    private val newUserCounter =  MutableLiveData("No data.")
    private val isLoading = MutableLiveData(false)

    fun refreshData(){
        isLoading.value = true
        userModel.refreshData(object : UserModel.OnDataReadyCallback {
            override fun onDataReady(data: String) {
                isLoading.value = false
                newUserCounter.value = data
            }
        })
    }
}
