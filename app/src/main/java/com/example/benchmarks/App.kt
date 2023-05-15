package com.example.benchmarks

import android.app.Application
import com.example.benchmarks.model.OperationsService

class App : Application() {
    val operationsService = OperationsService()
}