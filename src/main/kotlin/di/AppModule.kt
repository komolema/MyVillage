package di

import database.*
import database.dao.*
import database.dao.domain.*
import database.dao.audit.*
import org.koin.dsl.module
import security.VillageSecurityManager
import viewmodel.*

// Domain DAO module
val domainDaoModule = module {
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

// Audit DAO module
val auditDaoModule = module {
    single<DocumentsGeneratedDao> { DocumentsGeneratedDaoImpl() }
    single<ProofOfAddressDao> { ProofOfAddressDaoImpl() }
    single<UserDao> { UserDaoImpl() }
    single<RoleDao> { RoleDaoImpl() }
    single<PermissionDao> { PermissionDaoImpl() }
}

// DataBag module
val domainDataBagModule = module {
    single {
        DomainDataBag(
            // Domain DAOs
            addressDao = get(),
            animalDao = get(),
            dependantDao = get(),
            employmentDao = get(),
            leadershipDao = get(),
            managedByDao = get(),
            ownershipDao = get(),
            paymentDao = get(),
            qualificationDao = get(),
            residenceDao = get(),
            residentDao = get(),
            resourceDao = get(),
        )
    }
}

val auditDataBagModule = module {
    single {
        AuditDataBag(
            // Audit DAOs
            documentsGeneratedDao = get(),
            proofOfAddressDao = get(),
            userDao = get(),
            roleDao = get(),
            permissionDao = get()
        )
    }
}

val viewModelModule = module {
    factory { ResidentViewModel(get()) }
    factory {
        ResidentWindowViewModel(
            domainDataBag = get()
        )
    }
    factory { AnimalViewModel(get(), get(), get()) }
    factory { OnboardingViewModel() }
    factory { SettingsViewModel() }
    factory { LoginViewModel(get(), get()) }
}

val securityModule = module {
    // Create a single instance of SecurityManager with the DataBag injected
    single { VillageSecurityManager(get(), get()) }
}

val appModule = listOf(domainDaoModule, auditDaoModule, domainDataBagModule, auditDataBagModule, securityModule, viewModelModule)
