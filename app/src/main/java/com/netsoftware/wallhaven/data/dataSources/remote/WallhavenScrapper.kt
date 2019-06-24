package com.netsoftware.wallhaven.data.dataSources.remote

import `in`.nerd_is.wallhaven4kotlin.scrape.Scrape
import com.netsoftware.wallhaven.data.models.SearchConfig
import com.netsoftware.wallhaven.data.models.User.Companion.THUMB_ORIGINAL
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.utility.extensions.UrlHandler
import com.netsoftware.wallhaven.utility.extensions.convert
import com.netsoftware.wallhaven.utility.managers.MyDisplayManager
import okhttp3.HttpUrl
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

object WallhavenScrapper {
    private val base = HttpUrl.parse("https://alpha.wallhaven.cc")!!

    private fun createQuery(searchConfig: SearchConfig): String {
        val builder = base.newBuilder("search")
        builder?.addQueryParameter(SearchConfig.Q, searchConfig.q)
            ?.addQueryParameter(SearchConfig.CATEGORIES, searchConfig.categories)
            ?.addQueryParameter(SearchConfig.PURITY, searchConfig.purity)
            ?.addQueryParameter(SearchConfig.SORTING, searchConfig.sorting)
            ?.addQueryParameter(SearchConfig.ORDER, searchConfig.order)
            ?.addQueryParameter(SearchConfig.RESOLUTION_AT_LEAST, searchConfig.resolution_at_least)
            ?.addQueryParameter(SearchConfig.RESOLUTIONS, searchConfig.resolutions)
            ?.addQueryParameter(SearchConfig.RATIOS, searchConfig.ratios)
            ?.addQueryParameter(SearchConfig.COLORS, searchConfig.colors)
            ?.addQueryParameter(SearchConfig.PAGE, searchConfig.page)

        if (searchConfig.sorting == SearchConfig.SORTING_TOP_LIST) {
            builder?.addQueryParameter(SearchConfig.TOP_RANGE, searchConfig.top_range)
        }

        return builder?.build().toString()
    }


    fun search(searchConfig: SearchConfig): List<Wallpaper> {
        var result = listOf<Wallpaper>()
        try {
            result = Scrape.scrapeList(
                createQuery(
                    searchConfig
                )
            ).map {
                Wallpaper(
                    id = it.id.toString(),
                    resolution = MyDisplayManager.getResolution(it.resolution.width, it.resolution.height),
                    thumbs = mutableMapOf(THUMB_ORIGINAL to it.thumbnailUrl),
                    category = it.category.name,
                    purity = it.purity.name
                )
            }
        } catch (e: InterruptedIOException){
            if(e is SocketTimeoutException){ throw e}
        }
        return result
    }

    fun latest(searchConfig: SearchConfig): List<Wallpaper> {
        return search(searchConfig.copy(sorting = SearchConfig.SORTING_DATE_ADDED))
    }

    fun topList(searchConfig: SearchConfig): List<Wallpaper> {
        return search(searchConfig.copy(sorting = SearchConfig.SORTING_TOP_LIST))
    }

    fun random(searchConfig: SearchConfig): List<Wallpaper> {
        return search(searchConfig.copy(sorting = SearchConfig.SORTING_RANDOM))
    }

    fun wallpaper(id: String): Wallpaper{
        return id.toLongOrNull()?.let { Scrape.scrapeWallpaper(UrlHandler.fromWallpaperId(it)) }?.convert() ?: Wallpaper(id = id)
    }
}