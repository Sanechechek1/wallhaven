package com.netsoftware.wallhaven.data.dataSources.remote

import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.models.Wallpaper
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface WallhavenApi {
    @GET("settings")
    fun getUser(@Query("apikey") userApiKey: String): Single<User>

    @GET("w/{id}")
    fun getWallpaper(
        @Path("id") id: String,
        @Query("apikey") userApiKey: String? = SharedPrefs.getSharedPrefs().userApiKey.ifEmpty { null }
    ): Maybe<Wallpaper>

    @GET("search")
    fun getSearch(
        @QueryMap searchMap: Map<String, String>,
        @Query("apikey") userApiKey: String? = SharedPrefs.getSharedPrefs().userApiKey.ifEmpty { null }
    ): Single<List<Wallpaper>>
}