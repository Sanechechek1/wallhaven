package com.netsoftware.wallhaven.ui.main

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.badgeable.secondaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.mikepenz.essentialpack_typeface_library.EssentialPack
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
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
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        drawerInit()
    }

    private fun drawerInit() {
        DrawerBuilder()
            .withActivity(this)
            .withFullscreen(true)
            .addDrawerItems(
                PrimaryDrawerItem().withName("sdf").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                PrimaryDrawerItem().withName("sdf").withIcon(FontAwesome.Icon.faw_gamepad),
                PrimaryDrawerItem().withName("sdf").withIcon(FontAwesome.Icon.faw_eye)
            )
            .build()

        drawer = drawer {
            displayBelowStatusBar = false
            accountHeader {
                compactStyle = false
                background = R.drawable.drawer_header
            }
            primaryItem("Latest") {
                identifier = 1
//                iicon = EssentialPack.Icon.esp_time
                iconDrawable = IconicsDrawable(this@MainActivity)
                    .icon(EssentialPack.Icon.esp_time)
                    .color(Color.WHITE)
                    .sizeDp(24)
            }
            secondaryItem("Top List") { identifier = 2 }
            secondaryItem("Random") { identifier = 3 }
            secondaryItem("Favorites") { identifier = 4 }
            secondaryItem("Tags") { identifier = 5; enabled = false }
            divider {}
            secondaryItem("Settings") { identifier = 6 }
            secondaryItem("About") { identifier = 7 }
            onItemClick { _, _, drawerItem ->
                Toast.makeText(this@MainActivity, "${drawerItem.identifier}", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}
