package com.netsoftware.wallhaven.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.repositories.UserRepository
import com.netsoftware.wallhaven.utility.extensions.BaseViewModel
import io.reactivex.schedulers.Schedulers
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
        compositeDisposable.add(WallhavenApp.appComponent.getDB().userDao().insert(User(255)).subscribeOn(Schedulers.io()).subscribe {
                t -> Log.w("LOG", "User #$t added")
        }
        )



//        compositeDisposable.add(Single.fromCallable{
//            WallhavenApp.appComponent.getDB().userDao().insert(User(255))}
//            .subscribeOn(Schedulers.io())
//            .subscribe(
//                {
//                    Log.w("LOG", "User #$it added")
//                },{
//
//                }
//            )
//        )
    }
}
