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
import com.netsoftware.wallhaven.data.repositories.UserRepository
import com.netsoftware.wallhaven.databinding.MainFragmentBinding
import com.netsoftware.wallhaven.ui.adapters.WallpaperItem
import com.netsoftware.wallhaven.utility.extensions.addTo
import dagger.android.support.DaggerFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

class ViewerFragment : DaggerFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var userRepository: UserRepository
    private lateinit var binding: MainFragmentBinding
    private val compositeDisposable = CompositeDisposable()
    private val adapter = FlexibleAdapter<WallpaperItem>(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewerViewModel::class.java)

        WallhavenApp.appComponent.getWallhavenApi()
            .getUser("6kJO7b9FEEUOHpqRl6PZBBbjzkrfBkSY")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.w(WallhavenApp.TAG, "onCreateView: Fetched user = $it")
                },
                {
                    Log.w(WallhavenApp.TAG, "onCreateView: error = $it")
                    it.printStackTrace()
                }
            ).addTo(compositeDisposable)

        WallhavenApp.appComponent.getWallhavenApi()
            .getWallpaper("7zy509")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.w(WallhavenApp.TAG, "onCreateView: Fetched wall = $it")
                    adapter.addItem(WallpaperItem(it))
                },
                {
                    Log.w(WallhavenApp.TAG, "onCreateView: error = $it")
                    it.printStackTrace()
                }
            ).addTo(compositeDisposable)

        WallhavenApp.appComponent.getWallhavenApi()
            .getSearch(searchMap = mapOf("resolutions" to "1920x108"))
//            .getSearch(categories = 100)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.w(WallhavenApp.TAG, "onCreateView: Fetched search = $it")
                },
                {
                    Log.w(WallhavenApp.TAG, "onCreateView: error = $it")
                    it.printStackTrace()
                }
            ).addTo(compositeDisposable)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
        toolbar_menu_icon.setOnClickListener { (activity as MainActivity).openDrawer() }
        recycler_view.adapter = this.adapter
        recycler_view.layoutManager = SmoothScrollLinearLayoutManager(context)
        recycler_view.addItemDecoration(
            FlexibleItemDecoration(requireContext())
                .withOffset(8) // This helps when top items are removed!!
                .withEdge(true)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        fun newInstance() = ViewerFragment()
    }
}
