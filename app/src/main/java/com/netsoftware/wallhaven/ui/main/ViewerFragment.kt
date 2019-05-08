package com.netsoftware.wallhaven.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.WallhavenApp.Companion.TAG
import com.netsoftware.wallhaven.databinding.ViewerFragmentBinding
import com.netsoftware.wallhaven.ui.adapters.ProgressItem
import com.netsoftware.wallhaven.ui.adapters.WallpaperItem
import dagger.android.support.DaggerFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollStaggeredLayoutManager
import eu.davidea.flexibleadapter.items.IFlexible
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ViewerFragment : DaggerFragment(), FlexibleAdapter.EndlessScrollListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: ViewerFragmentBinding
    private val compositeDisposable = CompositeDisposable()
    private val adapter = FlexibleAdapter<IFlexible<*>>(mutableListOf(), null, true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.viewer_fragment, container, false)
        binding.viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewerViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel?.setType(arguments?.let { ViewerFragmentArgs.fromBundle(it).viewerType }
            ?: ViewerViewModel.ViewerType.LATEST_TYPE
        )

        adapter.setEndlessScrollListener(this, ProgressItem())
            .setEndlessScrollThreshold(8)
            .endlessPageSize = 24
        binding.recyclerView.adapter = this.adapter
        binding.recyclerView.layoutManager = SmoothScrollStaggeredLayoutManager(context, 2)
        binding.recyclerView.addItemDecoration(
            FlexibleItemDecoration(requireContext())
                .withOffset(8) // This helps when top items are removed!!
                .withEdge(true)
        )

        binding.toolbarTitle.text = arguments?.let { getString(ViewerFragmentArgs.fromBundle(it).viewerType.titleId) }
        binding.toolbarMenuIcon.setOnClickListener { (activity as MainActivity).openDrawer() }
        binding.toolbarFilterIcon.setOnClickListener { binding.viewModel?.loadLatest() }
        binding.viewModel?.getFetchedWalls()?.observe(
            viewLifecycleOwner,
            Observer {
                if (adapter.itemCount == 0) adapter.updateDataSet(it.map { wall -> WallpaperItem(wall) })
                else adapter.onLoadMoreComplete(it.map { wall -> WallpaperItem(wall) })
            })
        return binding.root
    }

    override fun noMoreLoad(newItemsSize: Int) {
        Log.d(TAG, "noMoreLoad: newItemsSize=$newItemsSize")
        Log.d(TAG, "noMoreLoad: Total pages loaded=${adapter.endlessCurrentPage}")
        Log.d(TAG, "noMoreLoad: Total items loaded=${adapter.mainItemCount}")
    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        binding.viewModel?.loadLatest(currentPage + 1)
        Log.d(TAG, "onLoadMore: lastPosition=$lastPosition")
        Log.d(TAG, "onLoadMore: currentPage=$currentPage")
        Log.d(TAG, "onLoadMore: Total pages loaded=${adapter.endlessCurrentPage}")
        Log.d(TAG, "onLoadMore: Total items loaded=${adapter.mainItemCount}")
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
