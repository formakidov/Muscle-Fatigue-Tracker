package com.promni.mft.di

import com.promni.mft.data.local.AppDatabase
import com.promni.mft.data.repository.ExpectedRecoveryRepositoryImpl
import com.promni.mft.data.repository.FatigueLogRepositoryImpl
import com.promni.mft.data.repository.MuscleRepositoryImpl
import com.promni.mft.domain.repository.ExpectedRecoveryRepository
import com.promni.mft.domain.repository.FatigueLogRepository
import com.promni.mft.domain.repository.MuscleRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    // Dao
    single { get<AppDatabase>().muscleDao() }
    single { get<AppDatabase>().fatigueLogDao() }
    single { get<AppDatabase>().expectedRecoveryDao() }

    // Repositories
    singleOf(::MuscleRepositoryImpl).bind<MuscleRepository>()
    singleOf(::ExpectedRecoveryRepositoryImpl).bind<ExpectedRecoveryRepository>()
    singleOf(::FatigueLogRepositoryImpl).bind<FatigueLogRepository>()
}
