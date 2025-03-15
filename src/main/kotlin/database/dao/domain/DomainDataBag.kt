package database.dao.domain

/**
 * A container class that holds all the DAOs for the application.
 * This reduces the number of dependencies that need to be injected into classes.
 */
class DomainDataBag(
    // Domain DAOs
    val addressDao: AddressDao,
    val animalDao: AnimalDao,
    val dependantDao: DependantDao,
    val employmentDao: EmploymentDao,
    val leadershipDao: LeadershipDao,
    val managedByDao: ManagedByDao,
    val ownershipDao: OwnershipDao,
    val paymentDao: PaymentDao,
    val qualificationDao: QualificationDao,
    val residenceDao: ResidenceDao,
    val residentDao: ResidentDao,
    val resourceDao: ResourceDao,
    

)