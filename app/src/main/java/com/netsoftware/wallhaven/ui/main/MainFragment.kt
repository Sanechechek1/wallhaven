package com.netsoftware.wallhaven.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.databinding.MainFragmentBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class MainFragment : DaggerFragment() {
    companion object {
        fun newInstance() = MainFragment()
    }
    private lateinit var binding : MainFragmentBinding

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        binding.viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        var user = User(null)
//        WallhavenApp.appComponent.getDB().userDao().insert(user)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this
    }
}
