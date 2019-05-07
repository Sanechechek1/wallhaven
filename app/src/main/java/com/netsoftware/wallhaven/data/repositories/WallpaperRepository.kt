package com.netsoftware.wallhaven.data.repositories

import `in`.nerd_is.wallhaven4kotlin.WallHaven
import com.netsoftware.wallhaven.data.dataSources.local.WallpaperDao
import com.netsoftware.wallhaven.data.dataSources.remote.WallhavenScrapper
import com.netsoftware.wallhaven.data.models.SearchConfig
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.utility.extensions.convert
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WallpaperRepository @Inject constructor(
    private val wpDao: WallpaperDao
) {
    fun getWallpaper(id: String): Maybe<Wallpaper> {
        return wpDao.getWallpaper(id).switchIfEmpty(Maybe.fromCallable {
            WallHaven.getWallpaper(id.toLong()).convert()
        })
    }

    fun getLatest(searchConfig: SearchConfig): Single<List<Wallpaper>> =
        Single.fromCallable { WallhavenScrapper.latest(searchConfig) }

    fun getTopList(searchConfig: SearchConfig): Single<List<Wallpaper>> =
        Single.fromCallable { WallhavenScrapper.topList(searchConfig) }

    fun getRandom(searchConfig: SearchConfig): Single<List<Wallpaper>> =
        Single.fromCallable { WallhavenScrapper.random(searchConfig) }

    fun saveWallpaper(wallpaper: Wallpaper): Single<Unit> {
        return Single.fromCallable { wpDao.insert(wallpaper) }.subscribeOn(Schedulers.io())
    }

}