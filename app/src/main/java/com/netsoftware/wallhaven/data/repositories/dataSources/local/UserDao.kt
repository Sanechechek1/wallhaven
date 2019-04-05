package com.netsoftware.wallhaven.data.repositories.dataSources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.netsoftware.wallhaven.data.models.User
import io.reactivex.Single

@Dao
interface UserDao{
    @Query("SELECT * from user")
    fun getAll(): Single<List<User>>

    @Query("SELECT * from user WHERE id = :id")
    fun getUser(id: Long): Single<User>

    @Insert(onConflict = REPLACE)
    fun insert(user: User): Single<Long>

    @Insert(onConflict = REPLACE)
    fun simpleInsert(user: User): Long

    @Query("DELETE from user")
    fun deleteAll()
}
