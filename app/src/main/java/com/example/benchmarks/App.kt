package com.example.benchmarks

import android.app.Application
import com.example.benchmarks.model.ListOperationsService
import com.example.benchmarks.model.MapsOperationsService

class App : Application() {
    val listOperationsService = ListOperationsService()
    val mapsOperationsService = MapsOperationsService()
}