package com.netsoftware.wallhaven.data.repositories

import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.repositories.dataSources.local.UserDao
import com.netsoftware.wallhaven.utility.managers.NetManager
import io.reactivex.Maybe
import javax.inject.Inject

class UserRepository @Inject constructor(val netManager: NetManager, val userDao: UserDao){
    fun getUser(id: Long): Maybe<User> {
        return userDao.getUser(id)
    }

    fun saveUser(user: User){
        userDao.insert(user)
    }
}