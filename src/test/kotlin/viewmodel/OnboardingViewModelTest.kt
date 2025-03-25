package viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import settings.UserRole

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private lateinit var viewModel: OnboardingViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OnboardingViewModel()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test completeOnboarding updates state`() {
        // Initial state should be false (from AppSettingsManager)
        // Act
        viewModel.completeOnboarding()

        // Assert
        assertTrue(viewModel.onboardingCompleted.value)
    }

    @Test
    fun `test updateUserRole updates role`() {
        // Act
        viewModel.updateUserRole(UserRole.ADMINISTRATOR)

        // Assert
        assertEquals(UserRole.ADMINISTRATOR, viewModel.userRole.value)
    }
}
