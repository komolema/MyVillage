package ui.screens.resident.tabs

enum class TabCompletionState {
    TODO,
    IN_PROGRESS,
    DONE
}

data class TabState(
    val completionState: TabCompletionState = TabCompletionState.TODO
)