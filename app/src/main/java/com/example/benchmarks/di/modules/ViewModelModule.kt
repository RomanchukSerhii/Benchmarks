package com.example.benchmarks.di.modules

import androidx.lifecycle.ViewModel
import com.example.benchmarks.di.annotations.ViewModelKey
import com.example.benchmarks.viewmodel.CollectionsFragmentViewModel
import com.example.benchmarks.viewmodel.MapsFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(CollectionsFragmentViewModel::class)
    @Binds
    fun bindCollectionsFragmentViewModel(impl: CollectionsFragmentViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MapsFragmentViewModel::class)
    @Binds
    fun bindMapsFragmentsViewModel(impl: MapsFragmentViewModel): ViewModel
}