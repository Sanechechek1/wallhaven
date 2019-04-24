package com.netsoftware.wallhaven.data.repositories

import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.dataSources.local.UserDao
import com.netsoftware.wallhaven.data.dataSources.remote.WallhavenApi
import com.netsoftware.wallhaven.utility.managers.NetManager
import io.reactivex.Single
import javax.inject.Inject



class UserRepository @Inject constructor(
    private val netManager: NetManager,
    private val userDao: UserDao,
    private val wallhavenApi: WallhavenApi
) {
    fun getUser(id: Long): Single<User> {
        return userDao.getUser(id)
            .flatMap { t ->
                if (!t.apiKey.isEmpty() && netManager.isConnectedToInternet == true) {
                    wallhavenApi.getUser(t.apiKey)
                        .map {
                            it.copy(id = t.id, apiKey = t.apiKey)
                        }
                        .doAfterSuccess{
                            userDao.simpleInsert(it)
                        }
                } else {
                    Single.just(t)
                }
            }
    }

    fun saveUser(user: User): Single<Long> {
        return userDao.insert(user)
    }
}