package com.netsoftware.wallpool.utility.extensions

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel

open class BaseViewModel: ViewModel(){
    val compositeDisposable = CompositeDisposable()
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun addDisposable(disposable: Disposable){
        compositeDisposable.add(disposable)
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        compositeDisposable.clear()
        coroutineScope.cancel()
        super.onCleared()
    }
}