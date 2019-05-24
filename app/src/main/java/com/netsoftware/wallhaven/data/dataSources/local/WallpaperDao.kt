package com.netsoftware.wallhaven.data.dataSources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netsoftware.wallhaven.data.models.Wallpaper
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface WallpaperDao{
    @Query("SELECT * from wallpaper")
    fun getAll(): Single<List<Wallpaper>>

    @Query("SELECT * from wallpaper WHERE id = :id")
    fun getWallpaper(id: String): Maybe<Wallpaper>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wallpaper: Wallpaper) : Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun simpleInsert(wallpaper: Wallpaper)

    @Query("DELETE from wallpaper")
    fun deleteAll()
}