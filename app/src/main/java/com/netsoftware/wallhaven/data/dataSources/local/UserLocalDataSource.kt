package com.netsoftware.wallhaven.data.dataSources.local

import com.netsoftware.wallhaven.data.daos.UserDao
import com.netsoftware.wallhaven.data.models.User
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(val userDao: UserDao, val sharedPrefs: SharedPrefs){
    fun getUser(){
        if(sharedPrefs.userId>0){

        }
        else{
            userDao.insert(User())
                .subscribeOn(Schedulers.io())
                .subscribe{
                        t->
                }
        }
    }

    fun saveUser(user: User){
        userDao.insert(user)
    }
}