package com.example.moviesocial

import android.app.Application
import com.example.moviesocial.dependencyinjection.managerModule
import com.example.moviesocial.dependencyinjection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MovieApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()

    }
    private fun initKoin(){
        startKoin{
            androidContext(this@MovieApplication)
            modules(listOf(viewModelModule, managerModule))
        }
    }
}