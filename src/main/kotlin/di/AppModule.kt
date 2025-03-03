package di

import database.dao.*
import org.koin.core.context.GlobalContext.get
import org.koin.dsl.module
import viewmodel.*

val daoModule = module {
    single<AddressDao> { AddressDaoImpl() }
    single<AnimalDao> { AnimalDaoImpl() }
    single<DependantDao> { DependantDaoImpl() }
    single<EmploymentDao> { EmploymentDaoImpl() }
    single<LeadershipDao> { LeadershipDaoImpl() }
    single<ManagedByDao> { ManagedByDaoImpl() }
    single<OwnershipDao> { OwnershipDaoImpl() }
    single<PaymentDao> { PaymentDaoImpl() }
    single<QualificationDao> { QualificationDaoImpl() }
    single<ResidenceDao> { ResidenceDaoImpl() }
    single<ResidentDao> { ResidentDaoImpl(get(), get()) }
    single<ResourceDao> { ResourceDaoImpl() }
}

val viewModelModule = module {
    factory { ResidentViewModel(get()) }
    factory { ResidentWindowViewModel(get(), get(), get()) }
}

val appModule = listOf(daoModule, viewModelModule)