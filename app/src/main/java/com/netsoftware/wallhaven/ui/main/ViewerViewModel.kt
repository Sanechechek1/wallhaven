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
) : BaseViewModel() {
    private val walls = mutableListOf<Wallpaper>()
    private val page = MutableLiveData(listOf<Wallpaper>())
    private val isLoading = MutableLiveData(false)
    private var type: ViewerType = ViewerType.SUITABLE_TYPE
    private var searchConfig: SearchConfig = SearchConfig()

    fun getWalls(): List<Wallpaper> = walls
    fun getPage(): LiveData<List<Wallpaper>> = page
    fun isLoading(): LiveData<Boolean> = isLoading

    fun init(type: ViewerType, searchConfig: SearchConfig = SearchConfig()){
        this.type = type
        this.searchConfig = searchConfig
        if (walls.isEmpty()) {
            loadByType(type)
        } else page.value = walls
    }

    private fun loadByType(type: ViewerType) {
        when (type) {
            //TODO: Think how need to sort suitable
            ViewerType.LATEST_TYPE, ViewerType.SUITABLE_TYPE -> loadLatest()
            ViewerType.TOPLIST_TYPE -> loadToplist()
            ViewerType.RANDOM_TYPE -> loadRandom()
            ViewerType.FAVORITES_TYPE -> TODO()
        }
    }

    fun refresh() {
        walls.clear()
        page.value = listOf()
        loadByType(type)
    }

    fun loadLatest(page: Int = 1) {
        isLoading.value = true
        wallpaperRepository.getLatest(searchConfig.copy(page = page.toString()))
                //TODO: extract duplicated configuration
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading.value = false }
            .retry(2)
            .subscribe({
                walls.addAll(it)
                this.page.value = it
            }, {
                Log.d(WallhavenApp.TAG, "loadLatest: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun loadToplist(page: Int = 1) {
        isLoading.value = true
        wallpaperRepository.getTopList(searchConfig.copy(page = page.toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading.value = false }
            .retry(2)
            .subscribe({
                walls.addAll(it)
                this.page.value = it
            }, {
                Log.d(WallhavenApp.TAG, "loadToplist: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun loadRandom(page: Int = 1) {
        isLoading.value = true
        wallpaperRepository.getRandom(searchConfig.copy(page = page.toString()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLoading.value = false }
            .retry(2)
            .subscribe({
                walls.addAll(it)
                this.page.value = it
            }, {
                Log.d(WallhavenApp.TAG, "loadRandom: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun fetchDetails(position: Int, callback: (wall: Wallpaper) -> Unit) {
        if (position >= 0 && position < walls.size && walls[position].url.isEmpty()) {
            wallpaperRepository.getWallpaper(walls[position].id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(2)
                .subscribe({
                    walls[position] = it
                    callback(it)
                }, {
                    Log.d(WallhavenApp.TAG, "fetchDetails: error = $it")
                    it.printStackTrace()
                })
                .addTo(compositeDisposable)
        }
    }

    enum class ViewerType(val titleId: Int) {
        SUITABLE_TYPE(R.string.title_suitable),
        LATEST_TYPE(R.string.title_latest),
        TOPLIST_TYPE(R.string.title_toplist),
        RANDOM_TYPE(R.string.title_random),
        FAVORITES_TYPE(R.string.title_favorites)
    }
}
