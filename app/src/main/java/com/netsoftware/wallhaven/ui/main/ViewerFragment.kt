package com.netsoftware.wallhaven.ui.main

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.WallhavenApp.Companion.TAG
import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs
import com.netsoftware.wallhaven.data.models.SearchConfig
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.databinding.ViewerFragmentBinding
import com.netsoftware.wallhaven.ui.adapters.ProgressItem
import com.netsoftware.wallhaven.ui.adapters.WallpaperItem
import com.netsoftware.wallhaven.ui.main.ViewerViewModel.ViewerType.*
import com.netsoftware.wallhaven.ui.views.ImageViewerOverlay
import com.netsoftware.wallhaven.utility.extensions.GlideApp
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.android.support.DaggerFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollStaggeredLayoutManager
import eu.davidea.flexibleadapter.items.IFlexible
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ViewerFragment : DaggerFragment(), FlexibleAdapter.EndlessScrollListener, FlexibleAdapter.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: ViewerFragmentBinding
    private val compositeDisposable = CompositeDisposable()
    private val adapter = FlexibleAdapter<IFlexible<*>>(mutableListOf(), this, true)
    private val viewerType = arguments?.let { ViewerFragmentArgs.fromBundle(it).viewerType } ?: SUITABLE_TYPE
    private var currentImage = -1

    private lateinit var imageViewer: StfalconImageViewer<Wallpaper>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //TODO: search will change searchConfig in viewModel and invoke refresh()
        val searchQuery = arguments?.let { ViewerFragmentArgs.fromBundle(it).searchQuery } ?: ""
        binding = DataBindingUtil.inflate(inflater, R.layout.viewer_fragment, container, false)
        binding.viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewerViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel?.init(viewerType,
            SearchConfig(q = searchQuery,
                resolution_at_least = WallhavenApp.appComponent.getSharedPrefs().screenResolution,
                ratios = WallhavenApp.appComponent.getSharedPrefs().screenRatio
            ))

        binding.toolbarTitle.text = getString(viewerType.titleId).format(searchQuery)
        binding.toolbarMenuIcon.setOnClickListener { (activity as MainActivity).openDrawer() }
        binding.refreshLayout.setColorSchemeResources(
            R.color.material_drawer_background, R.color.lime, R.color.orange, R.color.blue
        )
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setProgressBackgroundColorSchemeColor(
            resources.getColor(R.color.colorPrimary, context?.theme)
        )
        binding.refreshLayout.isRefreshing = true

        initRecyclerView()

        binding.viewModel?.getPage()?.observe(
            viewLifecycleOwner,
            Observer {
                if (adapter.itemCount == 0 || binding.refreshLayout.isRefreshing) {
                    adapter.updateDataSet(it.map { wall -> WallpaperItem(wall) })
                    if (it.isNotEmpty()) binding.refreshLayout.isRefreshing = false
                } else adapter.onLoadMoreComplete(it.map { wall -> WallpaperItem(wall) })
            })
        return binding.root
    }

    private fun initRecyclerView() {
        adapter.setEndlessScrollListener(this, ProgressItem())
            .setEndlessScrollThreshold(8)
            .endlessPageSize = 24
        binding.recyclerView.adapter = this.adapter
        binding.recyclerView.layoutManager = SmoothScrollStaggeredLayoutManager(
            context,
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        )
        binding.recyclerView.addItemDecoration(
            FlexibleItemDecoration(requireContext())
                .withOffset(3)
                .withEdge(true)
        )
    }

    override fun noMoreLoad(newItemsSize: Int) {
        Log.d(TAG, "noMoreLoad: newItemsSize=$newItemsSize")
        Log.d(TAG, "noMoreLoad: Total pages loaded=${adapter.endlessCurrentPage}")
        Log.d(TAG, "noMoreLoad: Total items loaded=${adapter.mainItemCount}")
    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        when (viewerType) {
            SUITABLE_TYPE -> binding.viewModel?.loadSuitable(currentPage + 1)
            LATEST_TYPE -> binding.viewModel?.loadLatest(currentPage + 1)
            TOPLIST_TYPE -> binding.viewModel?.loadToplist(currentPage + 1)
            RANDOM_TYPE -> binding.viewModel?.loadRandom(currentPage + 1)
            SEARCH_TYPE -> binding.viewModel?.loadSearch(currentPage + 1)
            FAVORITES_TYPE -> TODO()
        }
        Log.d(TAG, "onLoadMore: lastPosition=$lastPosition")
        Log.d(TAG, "onLoadMore: currentPage=$currentPage")
        Log.d(TAG, "onLoadMore: Total pages loaded=${adapter.endlessCurrentPage}")
        Log.d(TAG, "onLoadMore: Total items loaded=${adapter.mainItemCount}")
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        val currentWall = binding.viewModel?.getWalls()?.get(position) ?: Wallpaper()
        val loadingLiveData = MutableLiveData(false)
        val overlay = ImageViewerOverlay(context, currentWall, View.OnClickListener {
            imageViewer.updateTransitionImage(null)
            imageViewer.dismiss()
            (activity as MainActivity).navigateToSearch(it.tag.toString())
        }, loadingLiveData, viewLifecycleOwner)
        imageViewer = StfalconImageViewer.Builder<Wallpaper>(
            context,
            listOf(currentWall)
        ) { container: ImageView, wallpaper: Wallpaper ->
            showDetailedWall(container, wallpaper, loadingLiveData)
            if (wallpaper.url.isEmpty() && container.id != com.stfalcon.imageviewer.R.id.transitionImageView) {
                loadingLiveData.value = true
                binding.viewModel?.fetchDetails(position) {
                    showDetailedWall(container, it, loadingLiveData)
                    overlay.binding.wall = it
                }
            }
        }.withTransitionFrom(view?.findViewById(R.id.ivThumbnail))
            .withOverlayView(overlay)
            .withDismissListener { currentImage = -1 }
            .show()
        currentImage = position
        return false
    }

    private fun showDetailedWall(container: ImageView, wallpaper: Wallpaper, isLoading: MutableLiveData<Boolean>) {
        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
            ): Boolean {
                isLoading.value = false
                return false
            }

            override fun onResourceReady(
                resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                isLoading.value = false
                return false
            }

        }
        context?.let {
            if (wallpaper.url.isEmpty()) {
                GlideApp.with(it).load(wallpaper.thumbs[SharedPrefs.getSharedPrefs().thumbSize]).fitCenter()
                    .fitCenter()
                    .into(container)
            } else {
                GlideApp.with(it).load(wallpaper.url)
                    .thumbnail(
                        GlideApp.with(it)
                            .load(wallpaper.thumbs[SharedPrefs.getSharedPrefs().thumbSize]).fitCenter()
                    )
                    .fitCenter()
                    .listener(listener)
                    .into(container)
            }
        }
    }

    override fun onRefresh() {
        binding.viewModel?.refresh()
        adapter.updateDataSet(listOf())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        currentImage = savedInstanceState?.getInt("currentImage") ?: -1
        if (currentImage >= 0) onItemClick(null, currentImage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentImage", currentImage)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
