package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VerticalTabs(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
    selectedColor: Color = Color.White
) {
    Column(modifier = modifier) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTab
            Surface(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                color = if (isSelected) selectedColor else backgroundColor,
                contentColor = contentColorFor(backgroundColor),
                elevation = if (isSelected) 8.dp else 0.dp
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { onTabSelected(index) }
                )
            }
        }
    }
}