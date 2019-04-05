package com.netsoftware.wallhaven.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.data.repositories.UserRepository
import com.netsoftware.wallhaven.utility.extensions.BaseViewModel
import com.netsoftware.wallhaven.utility.extensions.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(private var userRepository: UserRepository): BaseViewModel() {
    val newUserCounter =  MutableLiveData("No data.")
    val isLoading = MutableLiveData(false)

    fun refreshData(){
        isLoading.value = true
        userRepository.getUser(1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                isLoading.value = false
            }
            .subscribe(
                {
                    Log.w(WallhavenApp.TAG, "onCreateView: $it ${Thread.currentThread().name}")
                },
                {
                    Log.w(WallhavenApp.TAG, "onCreateView: $it")
                }
            ).addTo(compositeDisposable)
    }
}
