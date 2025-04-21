package com.promni.mft.di

import com.promni.mft.domain.usecase.ChangeMuscleFatigueUseCase
import com.promni.mft.domain.usecase.GetFatigueLogsUseCase
import com.promni.mft.domain.usecase.GetMuscleInfoUseCase
import com.promni.mft.domain.usecase.SetTotalRecoveryTimeUseCase
import com.promni.mft.domain.util.MuscleInfoSorter
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {

    // Use Cases
    singleOf(::GetMuscleInfoUseCase)
    singleOf(::GetFatigueLogsUseCase)
    singleOf(::ChangeMuscleFatigueUseCase)
    singleOf(::SetTotalRecoveryTimeUseCase)

    // Sorting
    singleOf(::MuscleInfoSorter)
}
