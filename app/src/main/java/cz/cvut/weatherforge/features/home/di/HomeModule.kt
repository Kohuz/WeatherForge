package cz.cvut.weatherforge.features.home.di

import cz.cvut.weatherforge.features.home.presentation.HomeScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeScreenViewModel(get(), get()) }
}