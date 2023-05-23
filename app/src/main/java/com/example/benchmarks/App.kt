package com.example.benchmarks

import android.app.Application
import com.example.benchmarks.model.ListOperationsService

class App : Application() {
    val listOperationsService = ListOperationsService()
}