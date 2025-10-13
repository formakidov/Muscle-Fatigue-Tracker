package com.promni.mft.di

import com.promni.mft.domain.usecase.ChangeMuscleFatigueUseCase
import com.promni.mft.domain.usecase.DeleteFatigueLogUseCase
import com.promni.mft.domain.usecase.GetFatigueLogsUseCase
import com.promni.mft.domain.usecase.GetMuscleByIdUseCase
import com.promni.mft.domain.usecase.GetMuscleFilterUseCase
import com.promni.mft.domain.usecase.GetMusclesInfoUseCase
import com.promni.mft.domain.usecase.SetMuscleFilterUseCase
import com.promni.mft.domain.usecase.SetTotalRecoveryTimeUseCase
import com.promni.mft.domain.util.MuscleInfoSorter
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {

    // Use Cases
    singleOf(::GetMusclesInfoUseCase)
    singleOf(::GetFatigueLogsUseCase)
    singleOf(::ChangeMuscleFatigueUseCase)
    singleOf(::SetTotalRecoveryTimeUseCase)
    singleOf(::DeleteFatigueLogUseCase)
    singleOf(::GetMuscleFilterUseCase)
    singleOf(::SetMuscleFilterUseCase)
    singleOf(::GetMuscleByIdUseCase)

    // Sorting
    singleOf(::MuscleInfoSorter)
}
