package com.netsoftware.wallhaven.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.mikepenz.essentialpack_typeface_library.EssentialPack
import com.mikepenz.materialdrawer.Drawer
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.databinding.MainActivityBinding
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {
    lateinit var drawer: Drawer
    lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ViewerFragment.newInstance())
                .commitNow()
        }
        Thread.sleep(500)
        drawerInit()
    }

    private fun drawerInit() {
        drawer = drawer {
            displayBelowStatusBar = false
            accountHeader {
                compactStyle = false
                background = R.drawable.drawer_header
            }
            primaryItem("Latest") {
                identifier = 1
                iicon = EssentialPack.Icon.esp_time
            }
            primaryItem("Top List") {
                identifier = 2
                iicon = EssentialPack.Icon.esp_diamond
            }
            primaryItem("Random") {
                identifier = 3
                iicon = EssentialPack.Icon.esp_shuffle
            }
            primaryItem("Favorites") {
                identifier = 4
                iicon = EssentialPack.Icon.esp_like_2
            }
            primaryItem("Tags") {
                identifier = 5
                enabled = false
                disabledIconColorRes = R.color.material_drawer_icon_disabled
                iicon = EssentialPack.Icon.esp_price_tag
            }
            divider {}
            primaryItem("Settings") {
                identifier = 6
                iicon = EssentialPack.Icon.esp_settings_5
            }
            primaryItem("About") {
                identifier = 7
                iicon = EssentialPack.Icon.esp_info
            }
            onItemClick { _, _, drawerItem ->
                Toast.makeText(this@MainActivity, "${drawerItem.identifier}", Toast.LENGTH_SHORT).show()
                false
            }
        }
        drawer.header.isClickable = false
    }

    fun openDrawer(){
        drawer.openDrawer()
    }

    fun closeDrawer(){
        drawer.closeDrawer()
    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen) drawer.closeDrawer() else super.onBackPressed()
    }
}
