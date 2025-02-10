package di

import database.dao.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import viewmodel.*

val daoModule = module {
    single { AddressDao() }
    single { AnimalDao() }
    single { DependantDao() }
    single { EmploymentDao() }
    single { LeadershipDao() }
    single { ManagedByDao() }
    single { OwnershipDao() }
    single { PaymentDao() }
    single { QualificationDao() }
    single { ResidenceDao() }
    single { ResidentDao() }
    single { ResourceDao() }
}

val viewModelModule = module {
    viewModel { ResidentViewModel(get()) }
    viewModel { ResidenceViewModel() }
    viewModel { DependantViewModel() }
    viewModel { QualificationViewModel() }
    viewModel { EmploymentViewModel() }
    viewModel { ResidentWindowViewModel() }
}

val appModule = listOf(daoModule, viewModelModule)