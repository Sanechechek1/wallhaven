package com.netsoftware.wallhaven.ui.main

import android.annotation.SuppressLint
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
import com.netsoftware.wallhaven.WallhavenApp.Companion.TAG
import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs
import com.netsoftware.wallhaven.data.models.SearchConfig
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.databinding.ViewerFragmentBinding
import com.netsoftware.wallhaven.ui.adapters.ProgressItem
import com.netsoftware.wallhaven.ui.adapters.WallpaperItem
import com.netsoftware.wallhaven.ui.main.ViewerViewModel.ViewerType.SUITABLE_TYPE
import com.netsoftware.wallhaven.ui.views.ImageViewerOverlay
import com.netsoftware.wallhaven.ui.views.ResolutionPicker
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
    private var searchConfig = SearchConfig()

    private lateinit var imageViewer: StfalconImageViewer<Wallpaper>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchConfig = SearchConfig.configFromBundle(arguments)
        binding = DataBindingUtil.inflate(inflater, R.layout.viewer_fragment, container, false)
        binding.viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewerViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel?.init(viewerType, searchConfig)
        setupViews()

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

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        binding.toolbarTitle.text = getString(viewerType.titleId).format(searchConfig.q)
        binding.toolbarMenuIcon.setOnClickListener { (activity as MainActivity).openDrawer() }
        binding.refreshLayout.setColorSchemeResources(
            R.color.material_drawer_background, R.color.lime, R.color.orange, R.color.blue
        )
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setProgressBackgroundColorSchemeColor(
            resources.getColor(R.color.colorPrimary, context?.theme)
        )
        binding.refreshLayout.isRefreshing = true
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

        if (viewerType == SUITABLE_TYPE) {
//            binding.executePendingBindings()
//            (binding.filterContent.ratioChipgroup[0] as Chip).isChecked = true
//            binding.filterContent.resolutionValue.text = searchConfig.resolution_at_least
//            binding.filterContent.resolutionValue.isEnabled = false
//            binding.filterContent.resolutionSwitch.setOnTouchListener { _, _ -> true }
//            binding.filterContent.resolutionSwitch.alpha = 0.7F
//            binding.filterContent.resolutionValue.alpha = 0.6F
//            binding.filterContent.ratioChipgroup.alpha = 0.7F
//            for (chip in binding.filterContent.ratioChipgroup.children) {
//                chip.setOnTouchListener { _, _ -> true }
//            }
        }
        binding.filterContent.resolutionValue.setOnClickListener { showResolutionPicker() }
        binding.filterContent.resolutionSwitch.setOnSwitchListener { position, _ ->
            if (position == 0 && searchConfig.resolutions.isNotEmpty()) {
                searchConfig.resolution_at_least = searchConfig.resolutions.split(",").first()
                searchConfig.resolutions = ""
                binding.filterContent.resolutionValue.text = searchConfig.resolution_at_least
            }
        }
    }

    override fun noMoreLoad(newItemsSize: Int) {
        Log.d(TAG, "noMoreLoad: newItemsSize=$newItemsSize")
        Log.d(TAG, "noMoreLoad: Total pages loaded=${adapter.endlessCurrentPage}")
        Log.d(TAG, "noMoreLoad: Total items loaded=${adapter.mainItemCount}")
    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        binding.viewModel?.loadByType(currentPage + 1)
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
            //TODO: change to search configuring instead navigate to another fragment
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

    private fun showResolutionPicker() {
        context?.let {
            val resolutionPicker = ResolutionPicker(
                it,
                searchConfig.getResolutionList(),
                binding.filterContent.resolutionSwitch.getState(0)
            )
            resolutionPicker.setOnPositiveButtonClick {
                val checkedResolutions =
                    resolutionPicker.getPickedResolution().toString().replace(Regex("[\\[\\]\\s]"), "")
                binding.filterContent.resolutionValue.text =
                    SearchConfig.getReadableResolution(resolutionPicker.getPickedResolution()).ifEmpty {
                        getString(R.string.select)
                    }
                if (resolutionPicker.pickResolutionAtLeast)
                    searchConfig.resolution_at_least = checkedResolutions
                else searchConfig.resolutions = checkedResolutions
                resolutionPicker.dismiss()
            }
            resolutionPicker.setOnNegativeButtonClick {
                resolutionPicker.dismiss()
            }
            resolutionPicker.show()
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
