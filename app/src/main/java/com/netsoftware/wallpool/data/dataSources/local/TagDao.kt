package com.netsoftware.wallpool.data.dataSources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.netsoftware.wallpool.data.models.Tag
import io.reactivex.Single

@Dao
interface TagDao{
    @Query("SELECT * from tag")
    fun getAll(): Single<List<Tag>>

    @Query("SELECT * from tag WHERE id = :id")
    fun getTag(id: Long): Single<Tag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag: Tag): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun simpleInsert(tag: Tag): Long

    @Query("DELETE from tag")
    fun deleteAll()
}