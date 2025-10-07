package com.quitsmoking.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "quit_records")
data class QuitRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quitDate: LocalDateTime,
    val cigarettesPerDay: Int,
    val pricePerPack: Double,
    val cigarettesPerPack: Int = 20,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)