package com.netsoftware.wallhaven.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.secondaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
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
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        drawerInit()
    }

    private fun drawerInit() {
        drawer = drawer {
            displayBelowStatusBar = false
            accountHeader {
                compactStyle = false
                background = R.drawable.drawer_header
            }
            secondaryItem("Latest") { identifier = 1 }
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
