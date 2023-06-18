package com.example.benchmarks

import android.app.Application
import com.example.benchmarks.di.components.DaggerApplicationComponents
import com.example.benchmarks.model.ListOperationsService
import com.example.benchmarks.model.MapsOperationsService

class App : Application() {

    val component by lazy {
        DaggerApplicationComponents.create()
    }
    val listOperationsService = ListOperationsService()
    val mapsOperationsService = MapsOperationsService()
}