package com.lifevault.data.repository

import com.lifevault.data.dao.QuitRecordDao
import com.lifevault.data.model.QuitRecord
import kotlinx.coroutines.flow.Flow

class LifeVaultRepository(
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