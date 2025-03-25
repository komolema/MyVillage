package ui.screens.resource

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute

/**
 * JavaFX view for the resource management screen.
 */
class ResourceView : BorderPane(), KoinComponent {
    private val navigationManager = NavigationManager.getInstance()
    
    init {
        // Set up the resource content
        val resourceContent = createResourceContent()
        
        // Center the content in the view
        center = resourceContent
    }
    
    private fun createResourceContent(): VBox {
        val vbox = VBox(20.0)
        vbox.alignment = Pos.CENTER
        vbox.padding = Insets(50.0)
        vbox.maxWidth = 800.0
        
        // Title
        val titleLabel = Label("Resource Management")
        titleLabel.font = Font.font(28.0)
        titleLabel.textAlignment = TextAlignment.CENTER
        
        // Description
        val descriptionLabel = Label(
            "Manage your village resources efficiently. Track inventory, " +
            "monitor usage, and plan for future needs."
        )
        descriptionLabel.isWrapText = true
        descriptionLabel.textAlignment = TextAlignment.CENTER
        descriptionLabel.font = Font.font(16.0)
        
        // Back button
        val backButton = Button("Back to Dashboard")
        backButton.prefWidth = 200.0
        
        // Back button action
        backButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Dashboard)
        }
        
        // Add all components to the form
        vbox.children.addAll(
            titleLabel,
            descriptionLabel,
            backButton
        )
        
        return vbox
    }
}