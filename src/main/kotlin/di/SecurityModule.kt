package di

import org.koin.dsl.module
import security.VillageSecurityManager

/**
 * Koin module for security-related components.
 */
val securityModule = module {
    // Create a single instance of SecurityManager with the DataBag injected
    single { VillageSecurityManager(get(), get()) }
}