package com.promni.mft.di

import com.promni.mft.presentation.viewmodel.MusclesListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MusclesListViewModel)
}
