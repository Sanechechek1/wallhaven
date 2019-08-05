package com.netsoftware.wallpool.utility.di

import androidx.lifecycle.ViewModel
import com.netsoftware.wallpool.ui.main.AboutFragment
import com.netsoftware.wallpool.ui.main.ViewerFragment
import com.netsoftware.wallpool.ui.main.ViewerViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule{

    @ContributesAndroidInjector
    internal abstract fun mainActivityViewerFragment(): ViewerFragment

    @ContributesAndroidInjector
    internal abstract fun mainActivityAboutFragment(): AboutFragment

    @Binds
    @IntoMap
    @ViewModelKey(ViewerViewModel::class)
    abstract fun bindMainViewModel(viewModel: ViewerViewModel): ViewModel
}