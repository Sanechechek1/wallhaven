package com.netsoftware.wallhaven.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.netsoftware.wallhaven.data.models.User
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface UserDao{
    @Query("SELECT * from user")
    fun getAll(): Flowable<User>

    @Query("SELECT * from user WHERE id = :id")
    fun getUser(id: Long): Maybe<User>

    @Insert(onConflict = REPLACE)
    fun insert(user: User): Long

    @Query("DELETE from user")
    fun deleteAll()
}
