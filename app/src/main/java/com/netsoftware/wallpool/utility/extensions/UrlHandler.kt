package com.netsoftware.wallpool.utility.extensions

import okhttp3.HttpUrl

object UrlHandler {

    private const val BASE_URL = "https://alpha.wallpool.cc"
    private const val WALLPAPER_SEGMENT = "wallpaper"
    private val base = HttpUrl.parse(BASE_URL)!!

    fun fromWallpaperId(id: Long) =
        base.newBuilder(WALLPAPER_SEGMENT)
            ?.addPathSegment(id.toString())
            ?.build()
            ?.toString()!!
}