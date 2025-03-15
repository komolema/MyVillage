# DAO Transaction Migration Plan

## Overview

This document outlines the plan for migrating all DAO implementations to use the correct database transaction provider. The goal is to ensure that each DAO uses the appropriate database (domain or audit) for its operations.

## Implementation Details

### 1. TransactionProvider Interface

A `TransactionProvider` interface has been created to abstract the database transaction logic:

```kotlin
interface TransactionProvider {
    fun <T> executeTransaction(block: Transaction.() -> T): T
}
```

Two implementations of this interface are provided:

- `DomainTransactionProvider`: Uses the domain database for transactions
- `AuditTransactionProvider`: Uses the audit database for transactions

### 2. DAO Implementation Changes

Each DAO implementation should be updated to:

1. Accept a `TransactionProvider` as a constructor parameter with a default value of the appropriate provider
2. Replace all occurrences of `transaction { ... }` with `transactionProvider.executeTransaction { ... }`

Example for a domain DAO:

```kotlin
class SomeDaoImpl(
    private val transactionProvider: TransactionProvider = DomainTransactionProvider
) : SomeDao {
    override fun someMethod(): SomeResult = transactionProvider.executeTransaction {
        // Database operations
    }
}
```

Example for an audit DAO:

```kotlin
class SomeAuditDaoImpl(
    private val transactionProvider: TransactionProvider = AuditTransactionProvider
) : SomeAuditDao {
    override fun someMethod(): SomeResult = transactionProvider.executeTransaction {
        // Database operations
    }
}
```

### 3. Dependency Injection Updates

The AppModule.kt file should be updated to provide the appropriate TransactionProvider to each DAO:

```kotlin
// Domain DAO module
val domainDaoModule = module {
    single<AddressDao> { AddressDaoImpl(DomainTransactionProvider) }
    single<AnimalDao> { AnimalDaoImpl(DomainTransactionProvider) }
    // ... other domain DAOs
}

// Audit DAO module
val auditDaoModule = module {
    single<DocumentsGeneratedDao> { DocumentsGeneratedDaoImpl(AuditTransactionProvider) }
    single<ProofOfAddressDao> { ProofOfAddressDaoImpl(AuditTransactionProvider) }
    // ... other audit DAOs
}
```

### 4. Test Updates

For tests, a `TestTransactionProvider` has been created that uses the in-memory database:

```kotlin
class TestTransactionProvider : TransactionProvider {
    override fun <T> executeTransaction(block: Transaction.() -> T): T {
        return transaction {
            block()
        }
    }
}
```

Each DAO test should be updated to use this provider:

```kotlin
class SomeDaoTest {
    private val testTransactionProvider = TestTransactionProvider()
    private val someDao = SomeDaoImpl(testTransactionProvider)
    
    // Test methods
}
```

## Migration Checklist

### Domain DAOs

- [x] AnimalDao
- [ ] AddressDao
- [ ] DependantDao
- [ ] EmploymentDao
- [ ] LeadershipDao
- [ ] ManagedByDao
- [ ] OwnershipDao
- [ ] PaymentDao
- [ ] QualificationDao
- [ ] ResidenceDao
- [ ] ResidentDao
- [ ] ResourceDao

### Audit DAOs

- [ ] DocumentsGeneratedDao
- [ ] ProofOfAddressDao
- [ ] UserDao
- [ ] RoleDao
- [ ] PermissionDao

## Implementation Strategy

1. Update one DAO at a time, starting with the ones that have the fewest dependencies
2. Update the corresponding tests
3. Verify that the changes work correctly
4. Repeat for the next DAO

This incremental approach will minimize the risk of breaking the application and make it easier to identify and fix any issues that arise.