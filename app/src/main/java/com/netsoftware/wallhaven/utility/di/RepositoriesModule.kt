package com.netsoftware.wallhaven.utility.di

import com.netsoftware.wallhaven.data.repositories.UserRepository
import com.netsoftware.wallhaven.data.dataSources.local.UserDao
import com.netsoftware.wallhaven.data.dataSources.remote.WallhavenApi
import com.netsoftware.wallhaven.utility.managers.NetManager
import dagger.Module
import dagger.Provides

@Module
class RepositoriesModule{
    @Provides
    fun provideUserRepository(netManager: NetManager, userDao: UserDao, wallhavenApi: WallhavenApi): UserRepository {
        return UserRepository(netManager, userDao, wallhavenApi)
    }
}