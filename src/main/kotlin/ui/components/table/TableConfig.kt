package ui.components.table

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.TableColumnWidth

/**
 * Defines the type of input component to be used in a table cell
 */
enum class TableCellType {
    TEXT,
    NUMBER,
    DATE,
    DROPDOWN,
    RADIO,
    CHECKBOX
}

/**
 * Configuration for a single column in the generic table
 */
data class TableColumn<T>(
    val title: String,
    val width: TableColumnWidth = TableColumnWidth.Flex(1f),
    val type: TableCellType = TableCellType.TEXT,
    val getValue: (T) -> String,
    val setValue: (T, String) -> T,
    val options: List<String> = emptyList(), // For DROPDOWN and RADIO types
    val validation: (String) -> Boolean = { true }
)

/**
 * Configuration for the entire table
 */
data class TableConfig<T>(
    val columns: List<TableColumn<T>>,
    val rowHeight: Dp = 56.dp,
    val isEditable: Boolean = true,
    val onEdit: ((T) -> Unit)? = null,
    val onDelete: ((T) -> Unit)? = null,
    val onSave: ((T) -> Unit)? = null
)