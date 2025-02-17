package di

import database.dao.*
import org.koin.core.context.GlobalContext.get
import org.koin.core.module.dsl.factoryOf
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
    single<QualificationDao> { QualificationDaoImpl() }
    single<ResidenceDao> { ResidenceDaoImpl() }
    single { ResidentDao(get(), get()) }
    single { ResourceDao() }
}

val viewModelModule = module {
    factory { ResidentViewModel(get()) }
    factory { ResidenceViewModel() }
    factory { DependantViewModel() }
    factory { QualificationViewModel(get()) }
    factory { EmploymentViewModel() }
    factory { ResidentWindowViewModel() }
}

val appModule = listOf(daoModule, viewModelModule)