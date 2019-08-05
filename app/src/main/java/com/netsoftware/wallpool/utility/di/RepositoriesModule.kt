package com.netsoftware.wallpool.utility.di

import com.netsoftware.wallpool.data.dataSources.local.UserDao
import com.netsoftware.wallpool.data.dataSources.local.WallpaperDao
import com.netsoftware.wallpool.data.dataSources.remote.WallhavenApi
import com.netsoftware.wallpool.data.repositories.UserRepository
import com.netsoftware.wallpool.data.repositories.WallpaperRepository
import com.netsoftware.wallpool.utility.managers.NetManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoriesModule{

    @Singleton
    @Provides
    fun provideUserRepository(netManager: NetManager, userDao: UserDao, wallhavenApi: WallhavenApi): UserRepository {
        return UserRepository(netManager, userDao, wallhavenApi)
    }

    @Singleton
    @Provides
    fun provideWallpaperRepository(whDao: WallpaperDao, wallhavenApi: WallhavenApi): WallpaperRepository {
        return WallpaperRepository(whDao, wallhavenApi)
    }
}