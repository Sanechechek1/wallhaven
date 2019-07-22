package com.netsoftware.wallhaven.ui.main

import android.graphics.Bitmap
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
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ViewerViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val wallpaperRepository: WallpaperRepository
) : BaseViewModel() {
    private val walls = mutableListOf<Wallpaper>()
    private val page = MutableLiveData(listOf<Wallpaper>())
    private val isLoading = MutableLiveData(false)
    private val isError = MutableLiveData(false)
    private var type: ViewerType = ViewerType.SUITABLE_TYPE
    var fetchedWall = MutableLiveData<Wallpaper>()
    var savedPath = MutableLiveData<String>()
    var searchConfig: SearchConfig = SearchConfig()
    var defaultSearchConfig: SearchConfig = SearchConfig()

    fun getWalls(): List<Wallpaper> = walls
    fun getPage(): LiveData<List<Wallpaper>> = page
    fun isLoading(): LiveData<Boolean> = isLoading
    fun isError(): LiveData<Boolean> = isError

    fun init(type: ViewerType, searchConfig: SearchConfig = SearchConfig()) {
        this.type = type
        this.searchConfig = searchConfig
        this.defaultSearchConfig = searchConfig
        if (walls.isEmpty()) {
            loadByType()
        } else page.value = walls
    }

    fun loadByType(page: Int = 1) {
        when (type) {
            ViewerType.LATEST_TYPE, ViewerType.SUITABLE_TYPE -> loadLatest(page)
            ViewerType.TOPLIST_TYPE -> loadToplist(page)
            ViewerType.RANDOM_TYPE -> loadRandom(page)
            ViewerType.FAVORITES_TYPE -> TODO()
        }
    }

    fun refresh() {
        loadByType()
        walls.clear()
        page.value = listOf()
    }

    fun search(searchConfig: SearchConfig) {
        this.searchConfig = SearchConfig(searchConfig)
        refresh()
    }

    private fun loadLatest(page: Int = 1) {
        isLoading.value = true
        wallpaperRepository.getLatest(searchConfig.copy(page = page.toString()))
            .config()
            .addTo(compositeDisposable)
    }

    private fun loadToplist(page: Int = 1) {
        isLoading.value = true
        wallpaperRepository.getTopList(searchConfig.copy(page = page.toString()))
            .config()
            .addTo(compositeDisposable)
    }

    private fun loadRandom(page: Int = 1) {
        isLoading.value = true
        wallpaperRepository.getRandom(searchConfig.copy(page = page.toString()))
            .config()
            .addTo(compositeDisposable)
    }

    fun fetchDetails(position: Int){
        fetchedWall.value = walls[position]
        if (position >= 0 && position < walls.size && walls[position].tags.isEmpty()) {
            wallpaperRepository.getWallpaper(walls[position].id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(2)
                .subscribe({
                    walls[position] = it
                    fetchedWall.value = it
                }, {
                    Log.d(WallhavenApp.TAG, "fetchDetails: error = $it")
                    it.printStackTrace()
                })
                .addTo(compositeDisposable)
        }
    }

    fun saveToGallery(image: Bitmap, path: String) {
        savedPath.value = ""
        Single.fromCallable {
            var savedImagePath = ""
            val storageDir = File(path).parentFile
            var success = true
            if (!storageDir.exists()) {
                success = storageDir.mkdirs()
            }
            if (success) {
                val imageFile = File(path)
                savedImagePath = imageFile.absolutePath
                try {
                    val fOut = FileOutputStream(imageFile)
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                    fOut.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            savedImagePath
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                savedPath.value = it
            }, {
                savedPath.value = "error"
                Log.d(WallhavenApp.TAG, "saveToGallery: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    private fun Single<List<Wallpaper>>.config(): Disposable {
        return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry(1).subscribe({
                isLoading.value = false
                walls.addAll(it)
                page.value = it
                if (isError.value == true) isError.value = false
            }, {
                isLoading.value = false
                isError.value = true
                Log.d(WallhavenApp.TAG, "loadWalls: error = $it")
                it.printStackTrace()
            })
    }

    enum class ViewerType(val titleId: Int) {
        SUITABLE_TYPE(R.string.title_suitable),
        LATEST_TYPE(R.string.title_latest),
        TOPLIST_TYPE(R.string.title_toplist),
        RANDOM_TYPE(R.string.title_random),
        FAVORITES_TYPE(R.string.title_favorites)
    }
}
