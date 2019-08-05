package com.netsoftware.wallpool.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.WallpoolApp
import com.netsoftware.wallpool.databinding.AboutFragmentBinding
import dagger.android.support.DaggerFragment

class AboutFragment: DaggerFragment(), OnBackPressed{

    private lateinit var binding: AboutFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.about_fragment, container, false)
        binding.webview.loadUrl(getString(R.string.about_url))
        binding.toolbarMenuIcon.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }
        return binding.root
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webview.destroy()
    }

    companion object{
        val TITLE: String = WallpoolApp.appComponent.getAppContext().getString(R.string.title_about)
    }
}