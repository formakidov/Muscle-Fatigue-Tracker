package com.promni.mft.di

import com.promni.mft.data.local.AppDatabase
import com.promni.mft.data.repository.ExpectedRecoveryRepositoryImpl
import com.promni.mft.data.repository.FatigueLogRepositoryImpl
import com.promni.mft.data.repository.MuscleRepositoryImpl
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.usecase.ChangeMuscleFatigueUseCase
import com.promni.mft.domain.usecase.GetFatigueLogsUseCase
import com.promni.mft.domain.usecase.GetMuscleInfoUseCase
import com.promni.mft.domain.usecase.SetTotalRecoveryTimeUseCase
import com.promni.mft.presentation.viewmodel.MusclesListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {
    // Database
    single { AppDatabase.build(androidApplication()) }

    // Dao
    single { get<AppDatabase>().muscleDao() }
    single { get<AppDatabase>().fatigueLogDao() }
    single { get<AppDatabase>().expectedRecoveryDao() }

    // Repositories
    singleOf(::MuscleRepositoryImpl) { bind<MuscleRepository>() }
    singleOf(::ExpectedRecoveryRepositoryImpl) { bind<ExpectedRecoveryRepository>() }
    singleOf(::FatigueLogRepositoryImpl) { bind<FatigueLogRepository>() }

    // Use Cases
    singleOf(::GetMuscleInfoUseCase)
    singleOf(::GetFatigueLogsUseCase)
    singleOf(::ChangeMuscleFatigueUseCase)
    singleOf(::SetTotalRecoveryTimeUseCase)

    // View Models
    viewModelOf(::MusclesListViewModel)
}
