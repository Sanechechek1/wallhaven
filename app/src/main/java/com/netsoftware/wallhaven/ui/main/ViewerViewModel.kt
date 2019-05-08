package com.netsoftware.wallhaven.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netsoftware.wallhaven.R
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
    private val wallsLiveData = MutableLiveData(listOf<Wallpaper>())
    private val isLoading = MutableLiveData(false)
    private var type: ViewerType = ViewerType.LATEST_TYPE

    fun getFetchedWalls(): LiveData<List<Wallpaper>> = wallsLiveData
    fun isLoading(): LiveData<Boolean> = isLoading

    fun setType(type: ViewerType){
        this.type = type
        isLoading.value = true
        when(type){
            ViewerType.LATEST_TYPE -> loadLatest()
            ViewerType.TOPLIST_TYPE -> loadToplist()
            ViewerType.RANDOM_TYPE -> loadRandom()
            ViewerType.FAVORITES_TYPE -> TODO()
        }
    }

    fun loadLatest(page: Int = 1){
        wallpaperRepository.getLatest(SearchConfig(page = page.toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading.value = false }
            .subscribe({
                walls.addAll(it)
                wallsLiveData.value = it
            },{
                Log.d(WallhavenApp.TAG, "onCreateView: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun loadToplist(page: Int = 1){
        wallpaperRepository.getTopList(SearchConfig(page = page.toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading.value = false }
            .subscribe({
                walls.addAll(it)
                wallsLiveData.value = it
            },{
                Log.d(WallhavenApp.TAG, "onCreateView: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun loadRandom(page: Int = 1){
        wallpaperRepository.getRandom(SearchConfig(page = page.toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading.value = false }
            .subscribe({
                walls.addAll(it)
                wallsLiveData.value = it
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
    enum class ViewerType(val titleId: Int) {
        LATEST_TYPE(R.string.title_latest),
        TOPLIST_TYPE(R.string.title_toplist),
        RANDOM_TYPE(R.string.title_random),
        FAVORITES_TYPE(R.string.title_favorites)
    }
}
