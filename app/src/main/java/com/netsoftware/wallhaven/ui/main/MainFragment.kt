package com.netsoftware.wallhaven.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.WallhavenApp.Companion.TAG
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.databinding.MainFragmentBinding
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainFragment : DaggerFragment() {
    private lateinit var binding : MainFragmentBinding
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val compositeDisposable = CompositeDisposable()
    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
//        var user = User(null)
//        WallhavenApp.appComponent.getDB().userDao().insert(user)

        Log.w(TAG, "onCreateView: Thread ${Thread.currentThread().name} - ${Thread.currentThread().id}")
        compositeDisposable.add(WallhavenApp.appComponent.getWallhavenApi()
            .getUser("6kJO7b9FEEUOHpqRl6PZBBbjzkrfBkSY")
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    Log.w(TAG, "onCreateView: Thread ${Thread.currentThread().name} - ${Thread.currentThread().id}")
                    Log.w(TAG, "onCreateView: Fetched user = $it")
                    user = it
                    Log.w(TAG, "onCreateView: Assigned user = $user")
                },
                {

                }
            )
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
