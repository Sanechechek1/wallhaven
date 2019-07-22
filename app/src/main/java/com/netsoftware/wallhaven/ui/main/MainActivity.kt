package com.netsoftware.wallhaven.ui.main

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.essentialpack_typeface_library.EssentialPack
import com.mikepenz.materialdrawer.Drawer
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.databinding.MainActivityBinding
import com.netsoftware.wallhaven.ui.main.ViewerViewModel.ViewerType.*
import com.netsoftware.wallhaven.utility.extensions.changeColors
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*

class MainActivity : DaggerAppCompatActivity() {
    lateinit var drawer: Drawer
    lateinit var binding: MainActivityBinding
    lateinit var navController: NavController
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val titleMap = mapOf<String, Long>(
        SUITABLE_TYPE.name to 1,
        LATEST_TYPE.name to 2,
        TOPLIST_TYPE.name to 3,
        RANDOM_TYPE.name to 4,
        FAVORITES_TYPE.name to 5
    )
    var exitAppPressed = false
    var oopsCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        navController = Navigation.findNavController(binding.container.getViewById(R.id.nav_host_frag))
        navController.setGraph(
            R.navigation.nav_graph,
            ViewerFragmentArgs.Builder().setViewerType(SUITABLE_TYPE).build().toBundle()
        )
        drawerInit()
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.viewerFragment -> {
                    when (arguments?.let { ViewerFragmentArgs.fromBundle(it).viewerType }) {
                        SUITABLE_TYPE -> titleMap[SUITABLE_TYPE.name]?.let { drawer.setSelection(it, false) }
                        LATEST_TYPE -> titleMap[LATEST_TYPE.name]?.let { drawer.setSelection(it, false) }
                        TOPLIST_TYPE -> titleMap[TOPLIST_TYPE.name]?.let { drawer.setSelection(it, false) }
                        RANDOM_TYPE -> titleMap[RANDOM_TYPE.name]?.let { drawer.setSelection(it, false) }
                        FAVORITES_TYPE -> titleMap[FAVORITES_TYPE.name]?.let { drawer.setSelection(it, false) }
                        else -> drawer.deselect()
                    }
                }
                else -> TODO()
            }
        }
    }

    private fun drawerInit() {
        drawer = drawer {
            displayBelowStatusBar = false
            accountHeader {
                compactStyle = false
                background = R.drawable.drawer_header
            }
            primaryItem(getString(R.string.title_suitable)) {
                identifier = titleMap[SUITABLE_TYPE.name] ?: 1
                iicon = EssentialPack.Icon.esp_smartphone
            }
            primaryItem(getString(R.string.title_latest)) {
                identifier = titleMap[LATEST_TYPE.name] ?: 2
                iicon = EssentialPack.Icon.esp_time
            }
            primaryItem(getString(R.string.title_toplist)) {
                identifier = titleMap[TOPLIST_TYPE.name] ?: 3
                iicon = EssentialPack.Icon.esp_diamond
            }
            primaryItem(getString(R.string.title_random)) {
                identifier = titleMap[RANDOM_TYPE.name] ?: 4
                iicon = EssentialPack.Icon.esp_shuffle
            }
            primaryItem(getString(R.string.title_favorites)) {
                identifier = titleMap[FAVORITES_TYPE.name] ?: 5
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_like_2
            }
            primaryItem(getString(R.string.title_tags)) {
                //TODO: change to TagsFragment TAG
                identifier = titleMap["hereMustBeTAG"] ?: 6
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_price_tag
            }
            divider {}
            primaryItem(getString(R.string.title_settings)) {
                //TODO: change to SettingsFragment TAG
                identifier = titleMap["hereMustBeTAG"] ?: 7
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_settings_5
            }
            primaryItem(getString(R.string.title_about)) {
                //TODO: change to AboutFragment TAG
                identifier = titleMap["hereMustBeTAG"] ?: 8
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_info
            }
            onItemClick { _, _, drawerItem ->
                when (drawerItem.identifier) {
                    titleMap[SUITABLE_TYPE.name] -> navigateToViewer(SUITABLE_TYPE)
                    titleMap[LATEST_TYPE.name] -> navigateToViewer(LATEST_TYPE)
                    titleMap[TOPLIST_TYPE.name] -> navigateToViewer(TOPLIST_TYPE)
                    titleMap[RANDOM_TYPE.name] -> navigateToViewer(RANDOM_TYPE)
                    titleMap[FAVORITES_TYPE.name] -> navigateToViewer(FAVORITES_TYPE)
                }
                false
            }
        }
        drawer.header.setOnClickListener {
            oopsCounter++
            if (oopsCounter > 10) {
                oopsCounter = 0
                Snackbar.make(binding.root, getString(R.string.easterEggText), Snackbar.LENGTH_SHORT)
                    .changeColors().show()
            }
        }
    }

    fun openDrawer() {
        drawer.openDrawer()
    }

    //TODO: create query handler and delete this. Use @navigateToViewer instead
    fun navigateToSearch(tagId: String) {
        navController.navigate(
            ViewerFragmentDirections.goViewer().apply {
                viewerType = LATEST_TYPE
                //TODO: create QueryHelper
                searchQuery = "id:$tagId"
            })
    }

    fun navigateToViewer(type: ViewerViewModel.ViewerType, query: String = "") {
        val currentType =
            supportFragmentManager.findFragmentById(R.id.nav_host_frag)?.childFragmentManager?.primaryNavigationFragment
                ?.arguments?.let { ViewerFragmentArgs.fromBundle(it).viewerType }
        if ((currentType != null && currentType != type) || currentType == null) {
            navController.navigate(
                ViewerFragmentDirections.goViewer().apply {
                    viewerType = type
                    searchQuery = query
                })
        }
    }

    fun showInfo(info: String, onlyChangeText: Boolean) {
        binding.infoPanel.apply {
            if (!onlyChangeText || !isVisible) {
                text = info
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_up))
                visibility = View.VISIBLE
            } else {
                text = info
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_horisontal))
            }
        }
    }

    fun closeInfo() {
        binding.infoPanel.apply {
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_down))
            visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        val childFragmentManager =
            (supportFragmentManager.findFragmentById(R.id.nav_host_frag) as? NavHostFragment)?.childFragmentManager
        when {
            drawer.isDrawerOpen -> drawer.closeDrawer()
            childFragmentManager?.fragments?.get(0) is OnBackPressed -> {
                (childFragmentManager.fragments[0] as? OnBackPressed)
                    ?.onBackPressed()?.takeIf { !it }?.let {
                        if (childFragmentManager.backStackEntryCount == 0 && !exitAppPressed) {
                            showInfo(getString(R.string.backPressToExitApp), false)
                            exitAppPressed = true
                            coroutineScope.launch {
                                delay(2000)
                                synchronized(this) {
                                    exitAppPressed = false
                                    closeInfo()
                                }
                            }
                        } else super.onBackPressed()
                    }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
