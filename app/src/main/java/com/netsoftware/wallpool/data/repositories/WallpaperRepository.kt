package com.netsoftware.wallpool.data.repositories

import com.netsoftware.wallpool.data.dataSources.local.WallpaperDao
import com.netsoftware.wallpool.data.dataSources.remote.WallhavenApi
import com.netsoftware.wallpool.data.models.SearchConfig
import com.netsoftware.wallpool.data.models.Wallpaper
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WallpaperRepository @Inject constructor(
    private val wpDao: WallpaperDao,
    private val wallhavenApi: WallhavenApi
) {
    fun getWallpaper(id: String): Maybe<Wallpaper> {
        return wpDao.getWallpaper(id).switchIfEmpty(wallhavenApi.getWallpaper(id))
    }

    fun getLatest(searchConfig: SearchConfig): Single<List<Wallpaper>> =
        wallhavenApi.getSearch(searchConfig.copy(sorting = SearchConfig.SORTING_DATE_ADDED).toMap())

    fun getTopList(searchConfig: SearchConfig): Single<List<Wallpaper>> =
        wallhavenApi.getSearch(searchConfig.copy(sorting = SearchConfig.SORTING_TOP_LIST).toMap())

    fun getRandom(searchConfig: SearchConfig): Single<List<Wallpaper>> =
        wallhavenApi.getSearch(searchConfig.copy(sorting = SearchConfig.SORTING_RANDOM).toMap())

    fun getFavourites(): Single<List<Wallpaper>> =
        wpDao.getAll().subscribeOn(Schedulers.io())

    fun saveWallpaper(wallpaper: Wallpaper): Single<Unit> {
        return Single.fromCallable { wpDao.simpleInsert(wallpaper) }.subscribeOn(Schedulers.io())
    }

    fun deleteWallpaper(wallpaper: Wallpaper): Single<Unit> {
        return Single.fromCallable { wpDao.delete(wallpaper) }.subscribeOn(Schedulers.io())
    }

}