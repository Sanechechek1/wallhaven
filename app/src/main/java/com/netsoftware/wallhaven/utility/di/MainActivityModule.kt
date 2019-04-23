package com.netsoftware.wallhaven.utility.di

import androidx.lifecycle.ViewModel
import com.netsoftware.wallhaven.ui.main.ViewerFragment
import com.netsoftware.wallhaven.ui.main.ViewerViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule{

    @ContributesAndroidInjector
    internal abstract fun mainActivityFragment(): ViewerFragment

    @Binds
    @IntoMap
    @ViewModelKey(ViewerViewModel::class)
    abstract fun bindMainViewModel(viewModel: ViewerViewModel): ViewModel
}