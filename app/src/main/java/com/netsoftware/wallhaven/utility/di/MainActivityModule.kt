package com.netsoftware.wallhaven.utility.di

import androidx.lifecycle.ViewModel
import com.netsoftware.wallhaven.ui.main.MainFragment
import com.netsoftware.wallhaven.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule{

    @ContributesAndroidInjector
    internal abstract fun mainActivityFragment(): MainFragment

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}