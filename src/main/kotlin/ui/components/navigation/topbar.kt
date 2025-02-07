package ui.components.navigation

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ScreenWithAppBar(
    title: String,
    onBackClick: () -> Unit,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = backgroundColor,
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        content = { content() }
    )
}