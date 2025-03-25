package ui.screens.login

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute
import viewmodel.LoginViewModel

/**
 * JavaFX view for the login screen.
 */
class  LoginView : BorderPane(), KoinComponent {
    private val loginViewModel: LoginViewModel by inject()
    private val navigationManager = NavigationManager.getInstance()
    
    init {
        // Set up the login form
        val loginForm = createLoginForm()
        
        // Center the form in the view
        center = loginForm
        
        // Clear the form when the view is shown
        loginViewModel.clearForm()
    }
    
    private fun createLoginForm(): Parent {
        val vbox = VBox(20.0)
        vbox.alignment = Pos.CENTER
        vbox.padding = Insets(50.0)
        vbox.maxWidth = 400.0
        
        // Title
        val titleLabel = Label("Login")
        titleLabel.font = Font.font(24.0)
        
        // Username field
        val usernameField = TextField()
        usernameField.promptText = "Username"
        usernameField.textProperty().bindBidirectional(loginViewModel.usernameProperty)
        
        // Password field
        val passwordField = PasswordField()
        passwordField.promptText = "Password"
        passwordField.textProperty().bindBidirectional(loginViewModel.passwordProperty)
        
        // Error message
        val errorLabel = Label()
        errorLabel.textProperty().bind(loginViewModel.errorMessageProperty)
        errorLabel.style = "-fx-text-fill: red;"
        errorLabel.isVisible = false
        
        // Bind visibility to whether error message is empty
        loginViewModel.errorMessageProperty.addListener { _, _, newValue ->
            errorLabel.isVisible = newValue.isNotEmpty()
        }
        
        // Login button
        val loginButton = Button("Login")
        loginButton.prefWidth = 200.0
        loginButton.defaultButtonProperty().set(true)
        
        // Disable button when loading
        loginButton.disableProperty().bind(loginViewModel.isLoadingProperty)
        
        // Progress indicator
        val progressIndicator = ProgressIndicator()
        progressIndicator.prefHeight = 20.0
        progressIndicator.prefWidth = 20.0
        progressIndicator.visibleProperty().bind(loginViewModel.isLoadingProperty)
        
        // Login button action
        loginButton.setOnAction {
            if (loginViewModel.login()) {
                navigationManager.navigateTo(NavigationRoute.Dashboard)
            }
        }
        
        // Add all components to the form
        vbox.children.addAll(
            titleLabel,
            usernameField,
            passwordField,
            errorLabel,
            loginButton,
            progressIndicator
        )
        
        return vbox
    }
}