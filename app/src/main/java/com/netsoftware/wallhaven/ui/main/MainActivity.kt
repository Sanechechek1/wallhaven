package com.netsoftware.wallhaven.ui.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.mikepenz.essentialpack_typeface_library.EssentialPack
import com.mikepenz.materialdrawer.Drawer
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.databinding.MainActivityBinding
import com.netsoftware.wallhaven.ui.main.ViewerViewModel.ViewerType
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {
    lateinit var drawer: Drawer
    lateinit var binding: MainActivityBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        navController = Navigation.findNavController(this, R.id.nav_host)
        drawerInit()
    }

    private fun drawerInit() {
        drawer = drawer {
            displayBelowStatusBar = false
            accountHeader {
                compactStyle = false
                background = R.drawable.drawer_header
            }
            primaryItem(getString(R.string.title_latest)) {
                identifier = 1
                iicon = EssentialPack.Icon.esp_time
            }
            primaryItem(getString(R.string.title_toplist)) {
                identifier = 2
                iicon = EssentialPack.Icon.esp_diamond
            }
            primaryItem(getString(R.string.title_random)) {
                identifier = 3
                iicon = EssentialPack.Icon.esp_shuffle
            }
            primaryItem(getString(R.string.title_favorites)) {
                identifier = 4
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_like_2
            }
            primaryItem(getString(R.string.title_tags)) {
                identifier = 5
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_price_tag
            }
            divider {}
            primaryItem(getString(R.string.title_settings)) {
                identifier = 6
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_settings_5
            }
            primaryItem(getString(R.string.title_about)) {
                identifier = 7
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_info
            }
            onItemClick { _, _, drawerItem ->
                when (drawerItem.identifier) {
                    1L -> navController.navigate(
                        ViewerFragmentDirections.goViewer().apply {
                            viewerType = ViewerType.LATEST_TYPE
                        })
                    2L -> navController.navigate(
                        ViewerFragmentDirections.goViewer().apply {
                            viewerType = ViewerType.TOPLIST_TYPE
                        })
                    3L -> navController.navigate(
                        ViewerFragmentDirections.goViewer().apply {
                            viewerType = ViewerType.RANDOM_TYPE
                        })
                    4L -> navController.navigate(
                        ViewerFragmentDirections.goViewer().apply {
                            viewerType = ViewerType.FAVORITES_TYPE
                        })
                }
                false
            }
        }
        drawer.header.isClickable = false
    }

    fun openDrawer() {
        drawer.openDrawer()
    }

    fun closeDrawer() {
        drawer.closeDrawer()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) drawer.closeDrawer() else super.onBackPressed()
    }
}
