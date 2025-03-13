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
    single<ProofOfAddressDao> { ProofOfAddressDaoImpl() }
    single<QualificationDao> { QualificationDaoImpl() }
    single<ResidenceDao> { ResidenceDaoImpl() }
    single<ResidentDao> { ResidentDaoImpl(get(), get()) }
    single<ResourceDao> { ResourceDaoImpl() }
}

val viewModelModule = module {
    factory { ResidentViewModel(get()) }
    factory { ResidentWindowViewModel(
        qualificationDao = get(),
        residentDao = get(),
        dependantDao = get(),
        residenceDao = get(),
        addressDao = get(),
        employmentDao = get()
    ) }
    factory { AnimalViewModel(get(), get(), get()) }
    factory { OnboardingViewModel() }
    factory { SettingsViewModel() }
}

val appModule = listOf(daoModule, viewModelModule)
