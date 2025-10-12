package com.promni.mft.di

import com.promni.mft.data.local.entities.MuscleId
import com.promni.mft.presentation.viewmodel.MuscleDetailsViewModel
import com.promni.mft.presentation.viewmodel.MusclesListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MusclesListViewModel)
    factory { (muscleId: MuscleId) ->
        MuscleDetailsViewModel(
            muscleId = muscleId,
            getFatigueLogsUseCase = get(),
            setTotalRecoveryTimeUseCase = get(),
            changeFatigueUseCase = get(),
            deleteFatigueLogUseCase = get()
        )
    }
}
