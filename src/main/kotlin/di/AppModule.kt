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
    single<DependantDao> { DependantDaoImpl() }
    single { EmploymentDao() }
    single { LeadershipDao() }
    single { ManagedByDao() }
    single { OwnershipDao() }
    single { PaymentDao() }
    single<QualificationDao> { QualificationDaoImpl() }
    single<ResidenceDao> { ResidenceDaoImpl() }
    single<ResidentDao> { ResidentDaoImpl(get(), get()) }
    single { ResourceDao() }
}

val viewModelModule = module {
    factory { ResidentViewModel(get()) }
    factory { ResidentWindowViewModel(get(), get()) }
}

val appModule = listOf(daoModule, viewModelModule)