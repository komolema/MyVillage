package ui.navigation

import androidx.navigation.NavHostController

interface NavigationService {
    val navController: NavHostController
    
    fun navigateTo(route: NavigationRoute)
    fun navigateBack(): Boolean
    fun clearBackStack()
    
    companion object {
        fun create(navController: NavHostController): NavigationService = object : NavigationService {
            override val navController: NavHostController = navController
            
            override fun navigateTo(route: NavigationRoute) {
                navController.navigate(route.route)
            }
            
            override fun navigateBack(): Boolean {
                return navController.popBackStack()
            }
            
            override fun clearBackStack() {
                navController.popBackStack(navController.graph.startDestinationId, false)
            }
        }
    }
}