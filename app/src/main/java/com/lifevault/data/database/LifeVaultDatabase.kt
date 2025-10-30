package com.lifevault.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.lifevault.data.dao.QuitRecordDao
import com.lifevault.data.dao.UserProfileDao
import com.lifevault.data.dao.HabitDao
import com.lifevault.data.dao.UserDao
import com.lifevault.data.model.Converters
import com.lifevault.data.model.QuitRecord
import com.lifevault.data.model.UserProfile
import com.lifevault.data.model.Habit
import com.lifevault.data.model.User

@Database(
    entities = [
        QuitRecord::class,
        UserProfile::class,
        Habit::class,
        User::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LifeVaultDatabase : RoomDatabase() {

    abstract fun quitRecordDao(): QuitRecordDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun habitDao(): HabitDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: LifeVaultDatabase? = null

        fun getDatabase(context: Context): LifeVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeVaultDatabase::class.java,
                    "lifevault_database"
                )
                .fallbackToDestructiveMigration() // For development only
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}