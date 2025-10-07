package com.quitsmoking.data.dao

import androidx.room.*
import com.quitsmoking.data.model.QuitRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface QuitRecordDao {
    
    @Query("SELECT * FROM quit_records WHERE isActive = 1 ORDER BY createdAt DESC LIMIT 1")
    fun getActiveQuitRecord(): Flow<QuitRecord?>
    
    @Query("SELECT * FROM quit_records ORDER BY createdAt DESC")
    fun getAllQuitRecords(): Flow<List<QuitRecord>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuitRecord(quitRecord: QuitRecord): Long
    
    @Update
    suspend fun updateQuitRecord(quitRecord: QuitRecord)
    
    @Query("UPDATE quit_records SET isActive = 0 WHERE id != :activeId")
    suspend fun deactivateOtherRecords(activeId: Long)
    
    @Delete
    suspend fun deleteQuitRecord(quitRecord: QuitRecord)
    
    @Query("DELETE FROM quit_records")
    suspend fun deleteAllQuitRecords()
}