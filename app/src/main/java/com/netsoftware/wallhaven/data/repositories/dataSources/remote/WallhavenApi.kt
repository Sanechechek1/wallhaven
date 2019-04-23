package com.netsoftware.wallhaven.data.repositories.dataSources.remote

import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.models.Wallpaper
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WallhavenApi {
    @GET("settings")
    fun getUser(@Query("apikey") userApiKey: String): Single<User>

    @GET("w/{id}")
    fun getWallpaper(
        @Path("id") id: String,
        @Query("apikey") userApiKey: String? = null
    ): Single<Wallpaper>
}