package com.quitsmoking.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.quitsmoking.data.dao.QuitRecordDao
import com.quitsmoking.data.dao.UserProfileDao
import com.quitsmoking.data.dao.HabitDao
import com.quitsmoking.data.dao.UserDao
import com.quitsmoking.data.model.Converters
import com.quitsmoking.data.model.QuitRecord
import com.quitsmoking.data.model.UserProfile
import com.quitsmoking.data.model.Habit
import com.quitsmoking.data.model.User

@Database(
    entities = [
        QuitRecord::class,
        UserProfile::class,
        Habit::class,
        User::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ImNotDieDatabase : RoomDatabase() {
    
    abstract fun quitRecordDao(): QuitRecordDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun habitDao(): HabitDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: ImNotDieDatabase? = null
        
        fun getDatabase(context: Context): ImNotDieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImNotDieDatabase::class.java,
                    "im_not_die_database"
                )
                .fallbackToDestructiveMigration() // For development only
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}