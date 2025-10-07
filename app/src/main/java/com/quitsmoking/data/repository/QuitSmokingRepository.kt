package com.quitsmoking.data.repository

import com.quitsmoking.data.dao.QuitRecordDao
import com.quitsmoking.data.model.QuitRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuitSmokingRepository @Inject constructor(
    private val quitRecordDao: QuitRecordDao
) {
    
    fun getActiveQuitRecord(): Flow<QuitRecord?> = quitRecordDao.getActiveQuitRecord()
    
    fun getAllQuitRecords(): Flow<List<QuitRecord>> = quitRecordDao.getAllQuitRecords()
    
    suspend fun createNewQuitRecord(quitRecord: QuitRecord) {
        val id = quitRecordDao.insertQuitRecord(quitRecord)
        quitRecordDao.deactivateOtherRecords(id)
    }
    
    suspend fun updateQuitRecord(quitRecord: QuitRecord) {
        quitRecordDao.updateQuitRecord(quitRecord)
    }
    
    suspend fun deleteQuitRecord(quitRecord: QuitRecord) {
        quitRecordDao.deleteQuitRecord(quitRecord)
    }
    
    suspend fun deleteAllQuitRecords() {
        quitRecordDao.deleteAllQuitRecords()
    }
}