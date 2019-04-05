package com.netsoftware.wallhaven.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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

    private fun drawerInit(){
//        val headerResult = AccountHeaderBuilder()
//            .withActivity(this)
//            .withCompactStyle(false)
//            .withHeaderBackground(R.drawable.header)
//            .build()
        drawer = DrawerBuilder()
            .withActivity(this)
            .withDisplayBelowStatusBar(true)
            .withHeaderDivider(true)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(1).withName("Latest"),
                PrimaryDrawerItem().withIdentifier(1).withName("Random")
            )
            .withOnDrawerItemClickListener { view, position, drawerItem ->
                Toast.makeText(this,"BOOM", Toast.LENGTH_SHORT).show()
                true
            }
            .build()
    }
}
