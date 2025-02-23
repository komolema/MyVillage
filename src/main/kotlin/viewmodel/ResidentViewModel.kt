package viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import database.dao.ResidentDao
import ui.screens.resident.ResidentState
import java.util.*

class ResidentViewModel(private val residentDao: ResidentDao) {
    val PAGE_SIZE = 20

    sealed interface Intent {
        data class LoadResidents(val page: Int) : Intent
        data class Search(val query: String, val page: Int) : Intent
        data class DeleteResident(val id: UUID) : Intent
    }

    private val _state = MutableStateFlow(ResidentState())
    val state: StateFlow<ResidentState> = _state

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadResidents -> loadResidents(intent.page)
            is Intent.Search -> searchResidents(intent.query, intent.page)
            is Intent.DeleteResident -> deleteResident(intent.id)
        }
    }

    private fun deleteResident(id: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            residentDao.delete(id)
            loadResidents(0)
        }
    }

    private fun searchResidents(query: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val residents = residentDao.searchExpanded(query, page, PAGE_SIZE)
            _state.update { currentState ->
                currentState.copy(
                    residents = residents,
                    isLoading = false
                )
            }
        }
    }

    private fun loadResidents(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val residents = residentDao.getAllResidentExpanded(page, PAGE_SIZE)
            _state.update { currentState ->
                currentState.copy(
                    residents = residents,
                    isLoading = false
                )
            }
        }
    }
}