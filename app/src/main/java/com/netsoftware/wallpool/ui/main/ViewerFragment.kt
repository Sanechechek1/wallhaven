package com.netsoftware.wallpool.ui.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.mikepenz.essentialpack_typeface_library.EssentialPack
import com.mikepenz.essentialpackfilled_typeface_library.EssentialPackFilled
import com.mikepenz.iconics.IconicsDrawable
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.WallpoolApp
import com.netsoftware.wallpool.WallpoolApp.Companion.TAG
import com.netsoftware.wallpool.data.models.SearchConfig
import com.netsoftware.wallpool.data.models.Tag
import com.netsoftware.wallpool.data.models.Wallpaper
import com.netsoftware.wallpool.databinding.ViewerFragmentBinding
import com.netsoftware.wallpool.ui.adapters.ProgressItem
import com.netsoftware.wallpool.ui.adapters.WallpaperItem
import com.netsoftware.wallpool.ui.main.ViewerViewModel.ViewerType.FAVORITES_TYPE
import com.netsoftware.wallpool.ui.main.ViewerViewModel.ViewerType.SUITABLE_TYPE
import com.netsoftware.wallpool.ui.views.ImageViewerOverlay
import com.netsoftware.wallpool.ui.views.SetWallDialog
import com.netsoftware.wallpool.utility.extensions.changeColors
import com.netsoftware.wallpool.utility.managers.MyDisplayManager
import com.netsoftware.wallpool.utility.managers.NetManager
import com.netsoftware.wallpool.utility.managers.WallsUtil
import com.stfalcon.imageviewer.StfalconImageViewer
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import dagger.android.support.DaggerFragment
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollStaggeredLayoutManager
import eu.davidea.flexibleadapter.items.IFlexible
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlinx.serialization.json.JSON
import java.io.File
import javax.inject.Inject

class ViewerFragment : DaggerFragment(), FlexibleAdapter.EndlessScrollListener, FlexibleAdapter.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener, OnBackPressed {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var netManager: NetManager
    private lateinit var binding: ViewerFragmentBinding
    private lateinit var viewerType: ViewerViewModel.ViewerType
    private val compositeDisposable = CompositeDisposable()
    private val adapter = FlexibleAdapter<IFlexible<*>>(mutableListOf(), this, true)
    private var currentImage = -1
    private lateinit var category: ViewerViewModel.Category
    private val progressItem = ProgressItem()

    private lateinit var imageViewer: StfalconImageViewer<Wallpaper>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewerType = arguments?.let { ViewerFragmentArgs.fromBundle(it).viewerType } ?: SUITABLE_TYPE
        binding = DataBindingUtil.inflate(inflater, R.layout.viewer_fragment, container, false)
        binding.viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewerViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel?.apply {
            if (this.getWalls().isEmpty()) {
                binding.viewModel?.init(viewerType, SearchConfig.configFromBundle(arguments))
            } else if (adapter.isEmpty) {
                adapter.updateDataSet(this.getWalls().map { wall -> WallpaperItem(wall, viewerType) })
            }
        }
        category = arguments?.let { ViewerFragmentArgs.fromBundle(it).category } ?: ViewerViewModel.Category.NONE

        setupToolbar()
        setupRecyclerView()
        setupFilter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeObserves()
    }

    private fun setupToolbar() {
        binding.apply {
            toolbarTitle.setText(getTitleText())
            toolbarMenuIcon.setOnClickListener {
                (activity as MainActivity).openDrawer()
                WallpoolApp.hideKeyboard(it)
            }
            toolbarFilterIcon.setOnClickListener {
                if (filterView.isVisible) {
                    filterView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_top_to_up))
                    filterView.visibility = View.GONE
                } else {
                    filterView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_top_to_down))
                    filterView.visibility = View.VISIBLE
                }
            }
            toolbarSearchIcon.setOnClickListener {
                toolbarTitle.isEnabled = true
                toolbarTitle.setText("")
                toolbarTitle.requestFocus()
                WallpoolApp.showKeyboard(toolbarTitle)
            }
            toolbarTitle.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                    searchViewProcess(textView.text.trim().toString())
                false
            }
            if (viewerType == FAVORITES_TYPE) {
                toolbarSearchIcon.visibility = View.GONE
                toolbarFilterIcon.visibility = View.GONE
            } else if (category != ViewerViewModel.Category.NONE) {
                toolbarSearchIcon.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 4
            refreshLayout.setColorSchemeResources(
                R.color.material_drawer_background, R.color.lime, R.color.orange, R.color.blue
            )
            refreshLayout.setOnRefreshListener(this@ViewerFragment)
            refreshLayout.setProgressBackgroundColorSchemeColor(
                resources.getColor(R.color.colorPrimary, context?.theme)
            )
            adapter.setEndlessScrollListener(this@ViewerFragment, progressItem)
                .setEndlessScrollThreshold(14)
                .endlessPageSize = 24
            recyclerView.adapter = this@ViewerFragment.adapter
            recyclerView.layoutManager = SmoothScrollStaggeredLayoutManager(context, spanCount)
            recyclerView.addItemDecoration(
                FlexibleItemDecoration(requireContext())
            )
        }
    }

    private fun setupFilter() {
        binding.apply {
            val animation = {
                filterView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_top_to_up))
                filterView.visibility = View.GONE
            }
            val filterIcon = IconicsDrawable(
                requireContext(),
                EssentialPack.Icon.esp_controls
            ).sizePx(resources.getDimension(R.dimen.iicon_size).toInt())
            val filledFilterIcon = IconicsDrawable(
                requireContext(),
                EssentialPackFilled.Icon.esf_controls_filled
            ).sizePx(resources.getDimension(R.dimen.iicon_size).toInt())
            viewModel?.defaultSearchConfig?.let { filterView.defaultConfig = it }
            viewModel?.searchConfig?.let { filterView.searchConfig = it }
            toolbarFilterIcon.icon = if (filterView.hasChanges()) filledFilterIcon else filterIcon
            filterView.setOnPositiveButtonClick {
                startSearch(filterView.searchConfig)
                toolbarFilterIcon.icon = if (filterView.hasChanges()) filledFilterIcon else filterIcon
                animation()
            }
            filterView.setOnNegativeButtonClick {
                animation()
            }
            if (viewerType == SUITABLE_TYPE) {
                filterView.lockResolutionPicker()
            }
        }
    }

    private fun subscribeObserves() {
        binding.apply {
            viewModel?.getPage()?.observe(
                viewLifecycleOwner,
                Observer {
                    updateData(it)
                })
            viewModel?.isLoading()?.observe(viewLifecycleOwner, Observer {
                if (adapter.isEmpty || !it) refreshLayout.isRefreshing = it
            })
            viewModel?.isError()?.observe(viewLifecycleOwner, Observer {
                toggleNoInternetPlaceholder(it)
            })
        }
    }

    private fun updateData(walls: List<Wallpaper>) {
        if (viewerType != FAVORITES_TYPE && (adapter.itemCount == 0 || binding.refreshLayout.isRefreshing)) {
            adapter.updateDataSet(walls.map { wall -> WallpaperItem(wall, viewerType) })
            if (binding.viewModel?.isLoading()?.value != true) {
                toggleEmptyPlaceholder(walls.isEmpty())
            }
            if (!adapter.isEndlessScrollEnabled) adapter.setEndlessProgressItem(progressItem)
        } else adapter.onLoadMoreComplete(walls.map { wall -> WallpaperItem(wall, viewerType) })
    }

    private fun getTitleText(): String {
        return (if (category != ViewerViewModel.Category.NONE) {
            getString(category.titleId)
        } else if (!binding.viewModel?.searchConfig?.q.isNullOrEmpty()) {
            arguments?.let { bundle ->
                ViewerFragmentArgs.fromBundle(bundle).searchQuery?.let {
                    try {
                        JSON.parse(Tag.serializer(), it).name
                    } catch (e: Exception) {
                        it.capitalize()
                    }
                }
            }
        } else getString(viewerType.titleId)).toString()
    }

    override fun noMoreLoad(newItemsSize: Int) {
        Log.d(TAG, "noMoreLoad: newItemsSize=$newItemsSize")
        Log.d(TAG, "noMoreLoad: Total pages loaded=${adapter.endlessCurrentPage}")
        Log.d(TAG, "noMoreLoad: Total items loaded=${adapter.mainItemCount}")
    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        binding.viewModel?.loadByType(currentPage + 1)
    }

    override fun onItemClick(view: View?, position: Int): Boolean {
        binding.viewModel?.fetchDetails(position)
        val currentWall = binding.viewModel?.fetchedWall
        val overlay =
            ImageViewerOverlay(context, viewLifecycleOwner, currentWall, View.OnClickListener {
                imageViewer.updateTransitionImage(null)
                imageViewer.dismiss()
                (it as Chip).apply {
                    (activity as MainActivity).navigateToTagSearch(
                        JSON.stringify(
                            Tag.serializer(),
                            Tag(this.tag.toString().toLong(), this.text.toString())
                        )
                    )
                }
            })
        overlay.setFavoriteListener { wall, isAdded ->
            if (isAdded) binding.viewModel?.deleteFromFavourite(wall)
            else binding.viewModel?.saveToFavourite(wall)
        }
        overlay.setSaveImageListener { bitmap, path ->
            saveImage(overlay, bitmap, path)
        }
        overlay.setCropWallpaperListener { path ->
            checkPermission {
                val options = UCrop.Options().apply {
                    this.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL)
                    this.setToolbarTitle(getString(R.string.title_crop))
                    this.setCompressionQuality(80)
                }
                UCrop.of(Uri.parse(currentWall?.value?.url), Uri.fromFile(File(path).apply {
                    if (!this.parentFile.exists()) this.parentFile.mkdir()
                }))
                    .withAspectRatio(MyDisplayManager.getRatioX().toFloat(), MyDisplayManager.getRatioY().toFloat())
                    .withOptions(options)
                    .start(requireContext(), this@ViewerFragment)
            }
        }
        imageViewer = StfalconImageViewer.Builder<Wallpaper>(
            context,
            listOf(currentWall?.value)
        ) { container: ImageView, _ ->
            overlay.showDetailedWall(container)
        }.withTransitionFrom(view?.findViewById(R.id.ivThumbnail))
            .withOverlayView(overlay)
            .withDismissListener {
                currentImage = -1
            }
            .show()
        currentImage = position
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                UCrop.getOutput(it)?.let { resultUri ->
                    galleryAddPicScan(resultUri.path)
                    context?.let { context ->
                        SetWallDialog(context, resultUri)
                            .setBtnsClicks { wallUri, type ->
                                setWall(wallUri, type)
                            }.show()
                    }
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = data?.let { UCrop.getError(it) }
        }
    }

    private fun setWall(wallUri: Uri, type: WallsUtil.SetWallType) {
        (activity as MainActivity).showInfo(getString(R.string.info_setting_wallpaper), false)
        binding.viewModel?.coroutineScope?.launch {
            runBlocking {
                context?.let {
                    WallsUtil(it).setWall(
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, wallUri), type
                    )
                }
            }
            withContext(Dispatchers.Main) {
                (activity as MainActivity).showInfo(getString(type.changedText), true)
                delay(2000)
                (activity as MainActivity).closeInfo()
            }
        }
    }

    private fun saveImage(view: View, bitmap: Bitmap, path: String) {
        checkPermission {
            binding.viewModel?.saveToGallery(bitmap, path)
            binding.viewModel?.savedPath?.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    if (it.length > 10) {
                        galleryAddPicScan(it)
                        Snackbar.make(
                            view, getString(R.string.image_saved),
                            Snackbar.LENGTH_SHORT
                        ).changeColors().show()
                    }
                    binding.viewModel?.savedPath?.removeObservers(viewLifecycleOwner)
                }
            })
        }
    }

    private fun checkPermission(call: () -> Unit) {
        TedPermission.with(context)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    call()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    //nothing to do...
                }

            })
            .setDeniedMessage(context?.getString(R.string.permission_storage_denied))
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    private fun galleryAddPicScan(imagePath: String?) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context?.sendBroadcast(mediaScanIntent)

        MediaScannerConnection.scanFile(
            context, arrayOf(f.absolutePath), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

    override fun onRefresh() {
        adapter.updateDataSet(listOf())
        if (checkInternet()) binding.viewModel?.refresh()
        else toggleNoInternetPlaceholder(true)
    }

    private fun searchViewProcess(query: String) {
        binding.apply {
            toolbarTitle.clearFocus()
            val searchConfig = viewModel?.searchConfig?.let { SearchConfig(it) } ?: SearchConfig()
            if (query.trim().isNotEmpty()) {
                searchConfig.q = query.trim()
                toolbarTitle.setText(query)
            } else {
                searchConfig.q = viewModel?.defaultSearchConfig?.q.toString()
                toolbarTitle.setText(getString(viewerType.titleId).format(searchConfig.q))
                toolbarTitle.isEnabled = false
            }
            binding.apply {
                filterView.searchConfig.q = searchConfig.q
                filterView.defaultConfig.q = searchConfig.q
            }
            startSearch(searchConfig)
        }
    }

    private fun startSearch(searchConfig: SearchConfig) {
        if (searchConfig != binding.viewModel?.searchConfig) {
            adapter.updateDataSet(listOf())
            if (checkInternet()) binding.viewModel?.search(searchConfig)
        }
    }

    private fun checkInternet(): Boolean {
        return if (netManager.isConnectedToInternet) {
            toggleNoInternetPlaceholder(false)
            true
        } else {
            toggleNoInternetPlaceholder(true)
            binding.refreshLayout.isRefreshing = false
            false
        }
    }

    private fun toggleNoInternetPlaceholder(visible: Boolean) {
        val text = getString(R.string.placeholder_no_internet)
        if (visible && (binding.infoPlaceholder.isGone || binding.infoPlaceholder.text != text)) {
            binding.infoPlaceholder.apply {
                this.text = text
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_no_connection, 0, 0)
                visibility = View.VISIBLE
            }
        } else if (!visible && binding.infoPlaceholder.isVisible) {
            binding.infoPlaceholder.visibility = View.GONE
        }
    }

    private fun toggleEmptyPlaceholder(visible: Boolean) {
        val text = getString(R.string.placeholder_search_empty)
        if (visible && (binding.infoPlaceholder.isGone || binding.infoPlaceholder.text != text)) {
            binding.infoPlaceholder.apply {
                this.text = text
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_not_found, 0, 0)
                visibility = View.VISIBLE
            }
        } else if (!visible && binding.infoPlaceholder.isVisible) {
            binding.infoPlaceholder.visibility = View.GONE
        }
    }

    override fun onBackPressed(): Boolean {
        return if (binding.toolbarTitle.isFocused) {
            searchViewProcess(binding.viewModel?.searchConfig?.q.toString())
            true
        } else {
            false
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        currentImage = savedInstanceState?.getInt("currentImage") ?: -1
        if (currentImage >= 0) onItemClick(null, currentImage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentImage", currentImage)
//        outState.putBoolean("reinit", true)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
