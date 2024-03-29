package com.netsoftware.wallpool.data.repositories

import com.netsoftware.wallpool.data.dataSources.local.UserDao
import com.netsoftware.wallpool.data.dataSources.remote.WallhavenApi
import com.netsoftware.wallpool.data.models.User
import com.netsoftware.wallpool.utility.managers.NetManager
import io.reactivex.Single
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val netManager: NetManager,
    private val userDao: UserDao,
    private val wallhavenApi: WallhavenApi
) {
    fun getUser(id: String): Single<User> {
        return userDao.getUser(id)
            .flatMap { t ->
                if (t.apiKey.isNotEmpty() && netManager.isConnectedToInternet) {
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