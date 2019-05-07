package com.netsoftware.wallhaven.data.dataSources.remote

import `in`.nerd_is.wallhaven4kotlin.scrape.Scrape
import com.netsoftware.wallhaven.data.models.SearchConfig
import com.netsoftware.wallhaven.data.models.THUMB_ORIGINAL
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.utility.managers.MyDisplayManager
import okhttp3.HttpUrl

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
        return Scrape.scrapeList(
            createQuery(
                searchConfig
            )
        ).map {
            Wallpaper(
                id = it.id.toString(),
                resolution = MyDisplayManager.getResolution(it.resolution.width, it.resolution.height),
                thumbs = mutableMapOf(THUMB_ORIGINAL to it.thumbnailUrl)
            )
        }
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
}