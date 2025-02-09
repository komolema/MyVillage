package di

import database.dao.*
import org.koin.dsl.module

val daoModule = module {
    single {AddressDao()}
    single {AnimalDao() }
    single {DependantDao()}
    single {EmploymentDao()}
    single {LeadershipDao()}
    single {ManagedByDao()}
    single {OwnershipDao()}
    single {PaymentDao()}
    single {QualificationDao()}
    single {ResidenceDao()}
    single {ResidentDao()}
    single {ResourceDao()}
}