package com.lifevault.data.dao

import androidx.room.*
import com.lifevault.data.model.HabitEntry
import com.lifevault.data.model.HabitType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface HabitEntryDao {

    /**
     * Получить все записи для конкретной привычки
     */
    @Query("SELECT * FROM habit_entries WHERE habitType = :habitType ORDER BY timestamp DESC")
    fun getEntriesByType(habitType: HabitType): Flow<List<HabitEntry>>

    /**
     * Получить записи за конкретный день
     */
    @Query("""
        SELECT * FROM habit_entries
        WHERE habitType = :habitType
        AND date(timestamp) = date(:date)
        ORDER BY timestamp DESC
    """)
    suspend fun getEntriesForDay(habitType: HabitType, date: String): List<HabitEntry>

    /**
     * Получить записи за период
     */
    @Query("""
        SELECT * FROM habit_entries
        WHERE habitType = :habitType
        AND timestamp BETWEEN :startDate AND :endDate
        ORDER BY timestamp DESC
    """)
    suspend fun getEntriesBetween(
        habitType: HabitType,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<HabitEntry>

    /**
     * Получить все записи за сегодня (для всех привычек)
     */
    @Query("""
        SELECT * FROM habit_entries
        WHERE date(timestamp) = date('now', 'localtime')
        ORDER BY timestamp DESC
    """)
    fun getTodayEntries(): Flow<List<HabitEntry>>

    /**
     * Получить общее количество за день
     */
    @Query("""
        SELECT COALESCE(SUM(amount), 0)
        FROM habit_entries
        WHERE habitType = :habitType
        AND date(timestamp) = date(:date)
    """)
    suspend fun getTotalForDay(habitType: HabitType, date: String): Int

    /**
     * Получить количество записей за день
     */
    @Query("""
        SELECT COUNT(*)
        FROM habit_entries
        WHERE habitType = :habitType
        AND date(timestamp) = date(:date)
    """)
    suspend fun getEntriesCountForDay(habitType: HabitType, date: String): Int

    /**
     * Добавить новую запись
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: HabitEntry): Long

    /**
     * Добавить несколько записей
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<HabitEntry>)

    /**
     * Обновить запись
     */
    @Update
    suspend fun updateEntry(entry: HabitEntry)

    /**
     * Удалить запись
     */
    @Delete
    suspend fun deleteEntry(entry: HabitEntry)

    /**
     * Удалить запись по ID
     */
    @Query("DELETE FROM habit_entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: Long)

    /**
     * Удалить все записи для привычки
     */
    @Query("DELETE FROM habit_entries WHERE habitType = :habitType")
    suspend fun deleteEntriesByType(habitType: HabitType)

    /**
     * Удалить все записи
     */
    @Query("DELETE FROM habit_entries")
    suspend fun deleteAllEntries()

    /**
     * Получить последнюю запись для привычки
     */
    @Query("""
        SELECT * FROM habit_entries
        WHERE habitType = :habitType
        ORDER BY timestamp DESC
        LIMIT 1
    """)
    suspend fun getLastEntry(habitType: HabitType): HabitEntry?

    /**
     * Подсчитать серию дней без привычки (для алкоголя/курения)
     * Возвращает количество дней подряд, когда записей не было
     */
    @Query("""
        SELECT COUNT(DISTINCT date(timestamp))
        FROM habit_entries
        WHERE habitType = :habitType
        AND timestamp >= datetime('now', '-30 days')
    """)
    suspend fun getDaysWithEntries(habitType: HabitType): Int
}