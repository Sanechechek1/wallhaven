package com.netsoftware.wallpool.data.dataSources.local

import androidx.room.*
import com.netsoftware.wallpool.data.models.Wallpaper
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

    @Delete
    fun delete(wallpaper: Wallpaper)

    @Query("DELETE from wallpaper")
    fun deleteAll()
}