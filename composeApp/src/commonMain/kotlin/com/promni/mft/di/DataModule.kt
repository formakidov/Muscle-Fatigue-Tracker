package com.promni.mft.di

import com.promni.mft.data.local.AppDatabase
import com.promni.mft.data.local.provideDataStore
import com.promni.mft.data.repository.ExpectedRecoveryRepositoryImpl
import com.promni.mft.data.repository.FatigueLogRepositoryImpl
import com.promni.mft.data.repository.MuscleRepositoryImpl
import com.promni.mft.data.repository.UserDataRepositoryImpl
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import com.promni.mft.domain.repository.UserDataRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    // Database
    single { get<AppDatabase>().muscleDao() }
    single { get<AppDatabase>().fatigueLogDao() }
    single { get<AppDatabase>().expectedRecoveryDao() }

    // DataStore
    single { provideDataStore() }

    // Repositories
    singleOf(::MuscleRepositoryImpl).bind<MuscleRepository>()
    singleOf(::ExpectedRecoveryRepositoryImpl).bind<ExpectedRecoveryRepository>()
    singleOf(::FatigueLogRepositoryImpl).bind<FatigueLogRepository>()
    singleOf(::UserDataRepositoryImpl).bind<UserDataRepository>()
}
