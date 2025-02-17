package viewmodel

import androidx.lifecycle.ViewModel
import database.dao.QualificationDao
import models.Qualification
import java.util.*

class QualificationViewModel(private val qualificationDao: QualificationDao) {
    fun loadQualifications(residentId: UUID?): List<Qualification> {
        return residentId?.let {
            qualificationDao.getQualificationsByResidentId(it)
        } ?: emptyList()
    }

    fun createQualification(value: Qualification) {
        qualificationDao.createQualification(value)
    }

    fun updateQualification(value: Qualification) {
        qualificationDao.updateQualification(value)
    }
}