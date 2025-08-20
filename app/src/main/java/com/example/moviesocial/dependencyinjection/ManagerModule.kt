package com.example.moviesocial.dependencyinjection

import com.example.moviesocial.manager.StorageManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val managerModule =
    module {
        singleOf(::StorageManager)

    }