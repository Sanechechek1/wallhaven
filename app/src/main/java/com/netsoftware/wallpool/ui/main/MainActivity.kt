package com.netsoftware.wallpool.ui.main

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
import co.zsmb.materialdrawerkt.draweritems.badgeable.secondaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import co.zsmb.materialdrawerkt.draweritems.expandable.expandableItem
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.essentialpack_typeface_library.EssentialPack
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.databinding.MainActivityBinding
import com.netsoftware.wallpool.ui.main.ViewerViewModel.Category.*
import com.netsoftware.wallpool.ui.main.ViewerViewModel.ViewerType.*
import com.netsoftware.wallpool.utility.extensions.changeColors
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*

class MainActivity : DaggerAppCompatActivity() {
    lateinit var drawer: Drawer
    lateinit var binding: MainActivityBinding
    lateinit var navController: NavController
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val titleMap = listOf(
        SUITABLE_TYPE.name,
        LATEST_TYPE.name,
        TOPLIST_TYPE.name,
        RANDOM_TYPE.name,
        FAVORITES_TYPE.name,
        THREE_D.name,
        ABSTRACT.name,
        ANIME.name,
        ART.name,
        CARS.name,
        CITY.name,
        DARK.name,
        GIRLS.name,
        MINIMALISM.name,
        NATURE.name,
        SIMPLE.name,
        SPACE.name,
        LANDSCAPE.name,
        TEXTURE.name,
        AboutFragment.TITLE
    )
    var exitAppPressed = false
    var oopsCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        navController = Navigation.findNavController(binding.container.getViewById(R.id.nav_host_frag))
        navController.setGraph(
            R.navigation.nav_graph,
            ViewerFragmentArgs.Builder().setViewerType(SUITABLE_TYPE).setDrawerItemName(SUITABLE_TYPE.name).build().toBundle()
        )
        drawerInit()
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.viewerFragment -> {
                    arguments?.let { bundle ->
                        drawer.setSelection(
                            titleMap.indexOf(ViewerFragmentArgs.fromBundle(bundle).drawerItemName).toLong(),
                            false
                        )
                    }
                }
                R.id.aboutFragment -> drawer.setSelection(titleMap.indexOf(AboutFragment.TITLE).toLong(), false)
                else -> drawer.deselect()
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
                identifier = titleMap.indexOf(SUITABLE_TYPE.name).toLong()
                iicon = EssentialPack.Icon.esp_smartphone
            }
            primaryItem(getString(R.string.title_latest)) {
                identifier = titleMap.indexOf(LATEST_TYPE.name).toLong()
                iicon = EssentialPack.Icon.esp_time
            }
            primaryItem(getString(R.string.title_toplist)) {
                identifier = titleMap.indexOf(TOPLIST_TYPE.name).toLong()
                iicon = EssentialPack.Icon.esp_diamond
            }
            primaryItem(getString(R.string.title_random)) {
                identifier = titleMap.indexOf(RANDOM_TYPE.name).toLong()
                iicon = EssentialPack.Icon.esp_shuffle
            }
            primaryItem(getString(R.string.title_favorites)) {
                identifier = titleMap.indexOf(FAVORITES_TYPE.name).toLong()
                iicon = EssentialPack.Icon.esp_like_2
            }
            expandableItem {
                nameRes = R.string.title_categories
                selectable = false
                iicon = EssentialPack.Icon.esp_layers_1
                secondaryItem(getString(THREE_D.titleId)) {
                    level = 2; identifier = titleMap.indexOf(THREE_D.name).toLong()
                }
                secondaryItem(getString(ABSTRACT.titleId)) {
                    level = 2; identifier = titleMap.indexOf(ABSTRACT.name).toLong()
                }
                secondaryItem(getString(ANIME.titleId)) {
                    level = 2; identifier = titleMap.indexOf(ANIME.name).toLong()
                }
                secondaryItem(getString(ART.titleId)) { level = 2; identifier = titleMap.indexOf(ART.name).toLong() }
                secondaryItem(getString(CARS.titleId)) { level = 2; identifier = titleMap.indexOf(CARS.name).toLong() }
                secondaryItem(getString(CITY.titleId)) { level = 2; identifier = titleMap.indexOf(CITY.name).toLong() }
                secondaryItem(getString(DARK.titleId)) { level = 2; identifier = titleMap.indexOf(DARK.name).toLong() }
                secondaryItem(getString(GIRLS.titleId)) {
                    level = 2; identifier = titleMap.indexOf(GIRLS.name).toLong()
                }
                secondaryItem(getString(MINIMALISM.titleId)) {
                    level = 2; identifier = titleMap.indexOf(MINIMALISM.name).toLong()
                }
                secondaryItem(getString(NATURE.titleId)) {
                    level = 2; identifier = titleMap.indexOf(NATURE.name).toLong()
                }
                secondaryItem(getString(SIMPLE.titleId)) {
                    level = 2; identifier = titleMap.indexOf(SIMPLE.name).toLong()
                }
                secondaryItem(getString(SPACE.titleId)) {
                    level = 2; identifier = titleMap.indexOf(SPACE.name).toLong()
                }
                secondaryItem(getString(LANDSCAPE.titleId)) {
                    level = 2; identifier = titleMap.indexOf(LANDSCAPE.name).toLong()
                }
                secondaryItem(getString(TEXTURE.titleId)) {
                    level = 2; identifier = titleMap.indexOf(TEXTURE.name).toLong()
                }
            }
            divider {}
            primaryItem(AboutFragment.TITLE) {
                identifier = titleMap.indexOf(AboutFragment.TITLE).toLong()
                iicon = EssentialPack.Icon.esp_info
            }
            onItemClick { _, _, drawerItem ->
                if (drawerItem.identifier >= 0) {
                    when (drawerItem) {
                        is SecondaryDrawerItem -> {
                            val category = ViewerViewModel.Category.valueOf(titleMap[drawerItem.identifier.toInt()])
                            navigateToViewer(
                                LATEST_TYPE,
                                getString(category.queryId),
                                category
                            )
                        }
                        is PrimaryDrawerItem -> {
                            if (drawerItem.identifier == titleMap.indexOf(AboutFragment.TITLE).toLong()) {
                                navController.navigate(R.id.aboutFragment)
                            } else {
                                navigateToViewer(
                                    ViewerViewModel.ViewerType.valueOf(titleMap[drawerItem.identifier.toInt()]),
                                    category = NONE
                                )
                            }
                        }
                    }
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

    fun navigateToTagSearch(tag: String) {
        navController.navigate(
            ViewerFragmentDirections.goViewer().apply {
                viewerType = LATEST_TYPE
                drawerItemName = LATEST_TYPE.name
                searchQuery = tag
            })
    }

    fun navigateToViewer(
        type: ViewerViewModel.ViewerType,
        query: String = "",
        category: ViewerViewModel.Category
    ) {
        var currentQuery = ""
        val currentType =
            supportFragmentManager.findFragmentById(R.id.nav_host_frag)
                ?.childFragmentManager?.primaryNavigationFragment
                ?.arguments?.let { bundle ->
                ViewerFragmentArgs.fromBundle(bundle).let {
                    currentQuery = it.searchQuery.toString()
                    it.viewerType
                }
            }
        if ((currentType != null && (currentType != type || currentQuery != query)) || currentType == null) {
            navController.navigate(
                ViewerFragmentDirections.goViewer().apply {
                    viewerType = type
                    searchQuery = query
                    drawerItemName = if (category != NONE) category.name else type.name
                    this.category = category
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
        if (binding.infoPanel.isVisible) {
            binding.infoPanel.apply {
                startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_down))
                visibility = View.GONE
            }
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
