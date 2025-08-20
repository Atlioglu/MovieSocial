package com.example.moviesocial.dependencyinjection

import com.example.moviesocial.screens.favscreen.FavViewModel
import com.example.moviesocial.screens.settingsscreen.SettingsViewModel
import com.example.moviesocial.screens.detailsscreen.MovieDetailViewModel
import com.example.moviesocial.screens.bottomnavbarscreen.BottomNavigationBarViewModel
import com.example.moviesocial.screens.scratchscreen.ScratchLatestMovieViewModel
import com.example.moviesocial.screens.searchscreen.SearchViewModel
import com.example.moviesocial.screens.assistantscreen.AssistantViewModel
import com.example.moviesocial.screens.detailsscreen.HorrorMovieDetailViewModel
import com.example.moviesocial.screens.homescreen.HomeViewModel



import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::FavViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::MovieDetailViewModel)
        viewModelOf(::BottomNavigationBarViewModel)
        viewModelOf(::ScratchLatestMovieViewModel)
        viewModelOf(::SearchViewModel)
        viewModelOf(::AssistantViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::HorrorMovieDetailViewModel)

    }