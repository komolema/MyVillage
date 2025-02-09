package viewmodel

import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import database.dao.ResidentDao
import ui.screens.resident.ResidentState

class ResidentViewModel(private val residentDao: ResidentDao) {
    val PAGE_SIZE = 0

    sealed interface Intent {
        data class LoadResidents(val page: Int) : Intent
        data class Search(val query: String, val page: Int) : Intent
        object AddResident : Intent
    }

    private val _state = mutableStateOf(ResidentState())
    val state: Recomposer.State<ResidentState> = _state

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadResidents -> loadResidents(intent.page)
            is Intent.Search -> searchResidents(intent.query, intent.page)
            Intent.AddResident -> addResident()
        }
    }

    private fun searchResidents(query: String, page: Int) {

    }

    private fun addResident() {
        TODO("Not yet implemented")
    }

    private fun loadResidents(page: Int) {
        _state.value = _state.value.copy(
            residents = residentDao.getAll(page, PAGE_SIZE),
            isLoading = false
        )
    }

    // Similar implementations for search and add
}