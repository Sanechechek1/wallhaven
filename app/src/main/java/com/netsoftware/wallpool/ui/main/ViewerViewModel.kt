package com.netsoftware.wallpool.ui.main

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.WallpoolApp
import com.netsoftware.wallpool.data.models.SearchConfig
import com.netsoftware.wallpool.data.models.Wallpaper
import com.netsoftware.wallpool.data.repositories.UserRepository
import com.netsoftware.wallpool.data.repositories.WallpaperRepository
import com.netsoftware.wallpool.utility.extensions.BaseViewModel
import com.netsoftware.wallpool.utility.extensions.NoFirstLiveData
import com.netsoftware.wallpool.utility.extensions.addTo
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
    private val page = NoFirstLiveData<List<Wallpaper>>()
    private val isLoading = MutableLiveData(false)
    private val isError = MutableLiveData(false)
    private var type: ViewerType = ViewerType.SUITABLE_TYPE
    var fetchedWall = MutableLiveData<Wallpaper>()
    var savedPath = MutableLiveData<String>()
    var searchConfig: SearchConfig = SearchConfig()
    var defaultSearchConfig: SearchConfig = SearchConfig()

    fun getWalls(): List<Wallpaper> = walls
    fun getPage(): NoFirstLiveData<List<Wallpaper>> = page
    fun isLoading(): LiveData<Boolean> = isLoading
    fun isError(): LiveData<Boolean> = isError

    fun init(type: ViewerType, searchConfig: SearchConfig = SearchConfig()) {
        this.type = type
        this.searchConfig = searchConfig
        this.defaultSearchConfig = searchConfig.copy(q = "")
        if (walls.isEmpty()) {
            loadByType()
        } else page.value = walls
    }

    fun loadByType(page: Int = 1) {
        when (type) {
            ViewerType.LATEST_TYPE, ViewerType.SUITABLE_TYPE -> loadLatest(page)
            ViewerType.TOPLIST_TYPE -> loadToplist(page)
            ViewerType.RANDOM_TYPE -> loadRandom(page)
            ViewerType.FAVORITES_TYPE -> loadFavourites(page)
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
    }

    private fun loadToplist(page: Int = 1) {
        isLoading.value = true
        wallpaperRepository.getTopList(searchConfig.copy(page = page.toString()))
            .config()
    }

    private fun loadRandom(page: Int = 1) {
        isLoading.value = true
        wallpaperRepository.getRandom(searchConfig.copy(page = page.toString()))
            .config()
    }

    private fun loadFavourites(page: Int = 1) {
        if (page == 1) {
            isLoading.value = true
            wallpaperRepository.getFavourites()
                .observeOn(AndroidSchedulers.mainThread())
                .config()
        }
    }

    fun fetchDetails(position: Int) {
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
                    Log.d(WallpoolApp.TAG, "fetchDetails: error = $it")
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
                Log.d(WallpoolApp.TAG, "saveToGallery: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun saveToFavourite(wall: Wallpaper) {
        wallpaperRepository.saveWallpaper(wall)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                Log.d(WallpoolApp.TAG, "saveToFavourite: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun deleteFromFavourite(wall: Wallpaper) {
        wallpaperRepository.deleteWallpaper(wall)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                Log.d(WallpoolApp.TAG, "deleteFromFavourite: error = $it")
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
                Log.d(WallpoolApp.TAG, "loadWalls: error = $it")
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    enum class ViewerType(val titleId: Int) {
        SUITABLE_TYPE(R.string.title_suitable),
        LATEST_TYPE(R.string.title_latest),
        TOPLIST_TYPE(R.string.title_toplist),
        RANDOM_TYPE(R.string.title_random),
        FAVORITES_TYPE(R.string.title_favorites)
    }

    enum class Category(val titleId: Int, val queryId: Int) {
        THREE_D(R.string.category_3d, R.string.category_3d_query),
        ABSTRACT(R.string.category_abstract, R.string.category_abstract_query),
        ANIME(R.string.category_anime, R.string.category_anime_query),
        ART(R.string.category_art, R.string.category_art_query),
        CARS(R.string.category_cars, R.string.category_cars_query),
        CITY(R.string.category_city, R.string.category_city_query),
        DARK(R.string.category_dark, R.string.category_dark_query),
        GIRLS(R.string.category_girls, R.string.category_girls_query),
        MINIMALISM(R.string.category_minimalism, R.string.category_minimalism_query),
        NATURE(R.string.category_nature, R.string.category_nature_query),
        SIMPLE(R.string.category_simple, R.string.category_simple_query),
        SPACE(R.string.category_space, R.string.category_space_query),
        LANDSCAPE(R.string.category_landscape, R.string.category_landscape_query),
        TEXTURE(R.string.category_texture, R.string.category_texture_query),
        NONE(R.string.category_none_query, R.string.category_none_query),
    }
}
