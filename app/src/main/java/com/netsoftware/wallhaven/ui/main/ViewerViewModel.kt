package com.netsoftware.wallhaven.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.data.models.SearchConfig
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.data.repositories.UserRepository
import com.netsoftware.wallhaven.data.repositories.WallpaperRepository
import com.netsoftware.wallhaven.utility.extensions.BaseViewModel
import com.netsoftware.wallhaven.utility.extensions.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ViewerViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val wallpaperRepository: WallpaperRepository
): BaseViewModel() {
    private val walls = mutableListOf<Wallpaper>()
    private val wallsLiveData = MutableLiveData(walls)
    private val isLoading = MutableLiveData(false)
//    private var currentPage = 1

    init {
//        loadLatest()
    }

    fun getFetchedWalls(): LiveData<MutableList<Wallpaper>> = wallsLiveData
    fun isLoading(): LiveData<Boolean> = isLoading

    fun loadLatest(page: Int = 1){
        isLoading.value = true
        wallpaperRepository.getLatest(SearchConfig(page = page.toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading.value = false }
            .subscribe({
                walls.addAll(walls.size, it)
                wallsLiveData.value = walls
            },{
                Log.d(WallhavenApp.TAG, "onCreateView: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun refreshData(){
        isLoading.value = true
        userRepository.getUser(1).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                isLoading.value = false
            }
            .subscribe(
                {
                    Log.d(WallhavenApp.TAG, "onCreateView: $it ${Thread.currentThread().name}")
                },
                {
                    Log.d(WallhavenApp.TAG, "onCreateView: $it")
                }
            ).addTo(compositeDisposable)
    }
}
