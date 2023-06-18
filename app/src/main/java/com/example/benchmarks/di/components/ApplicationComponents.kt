package com.example.benchmarks.di.components

import com.example.benchmarks.di.annotations.ApplicationScope
import com.example.benchmarks.di.modules.ViewModelModule
import com.example.benchmarks.view.screens.CollectionsFragment
import com.example.benchmarks.view.screens.MapsFragment
import dagger.Component

@ApplicationScope
@Component(modules = [ViewModelModule::class])
interface ApplicationComponents {

    fun inject(fragment: CollectionsFragment)

    fun inject(fragment: MapsFragment)
}