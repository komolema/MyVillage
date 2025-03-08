package ui.navigation

import androidx.navigation.NavHostController
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals
import kotlin.test.assertNull
import java.util.UUID
import ui.screens.resident.WindowMode

class NavigationTest {
    private lateinit var mockNavController: NavHostController
    private lateinit var navigationService: NavigationService

    @BeforeEach
    fun setup() {
        mockNavController = mockk(relaxed = true)
        navigationService = NavigationService.create(mockNavController)
    }

    @Test
    fun `test navigation route creation`() {
        assertEquals("dashboard", NavigationRoute.Dashboard.route)
        assertEquals("resident", NavigationRoute.Resident.route)
        assertEquals("animal", NavigationRoute.Animal.route)
        assertEquals("resource", NavigationRoute.Resource.route)
        assertEquals("admin", NavigationRoute.Admin.route)
        assertEquals("settings", NavigationRoute.Settings.route)
    }

    @Test
    fun `test resident detail route with parameters`() {
        val uuid = UUID.randomUUID()
        val route = NavigationRoute.ResidentDetail(uuid, WindowMode.UPDATE)
        assertEquals("resident/$uuid?mode=update", route.route)
    }

    @Test
    fun `test resident detail route without parameters`() {
        val route = NavigationRoute.ResidentDetail()
        assertEquals("resident/?mode=view", route.route)
    }

    @Test
    fun `test fromRoute with valid routes`() {
        assertEquals(NavigationRoute.Dashboard, NavigationRoute.fromRoute("dashboard"))
        assertEquals(NavigationRoute.Resident, NavigationRoute.fromRoute("resident"))
        assertEquals(NavigationRoute.Animal, NavigationRoute.fromRoute("animal"))
        assertEquals(NavigationRoute.Resource, NavigationRoute.fromRoute("resource"))
        assertEquals(NavigationRoute.Admin, NavigationRoute.fromRoute("admin"))
        assertEquals(NavigationRoute.Settings, NavigationRoute.fromRoute("settings"))
    }

    @Test
    fun `test fromRoute with valid resident detail route`() {
        val uuid = UUID.randomUUID()
        val route = "resident/$uuid?mode=update"
        val result = NavigationRoute.fromRoute(route) as? NavigationRoute.ResidentDetail
        assertEquals(uuid, result?.residentId)
        assertEquals(WindowMode.UPDATE, result?.mode)
    }

    @Test
    fun `test fromRoute with invalid route`() {
        assertNull(NavigationRoute.fromRoute("invalid_route"))
    }

    @Test
    fun `test fromRoute with malformed UUID`() {
        val route = "resident/invalid-uuid?mode=view"
        assertNull(NavigationRoute.fromRoute(route))
    }

    @Test
    fun `test fromRoute with invalid mode`() {
        val uuid = UUID.randomUUID()
        val route = "resident/$uuid?mode=invalid_mode"
        assertNull(NavigationRoute.fromRoute(route))
    }

    @Test
    fun `test navigation service navigate`() {
        navigationService.navigateTo(NavigationRoute.Dashboard)
        verify { mockNavController.navigate("dashboard") }
    }

    @Test
    fun `test navigation service navigate back`() {
        every { mockNavController.popBackStack() } returns true
        assertEquals(true, navigationService.navigateBack())
        verify { mockNavController.popBackStack() }
    }

    @Test
    fun `test navigation service clear back stack`() {
        every { mockNavController.graph.startDestinationId } returns 1
        navigationService.clearBackStack()
        verify { mockNavController.popBackStack(1, false) }
    }
}
