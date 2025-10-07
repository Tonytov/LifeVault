package com.quitsmoking.di

import android.content.Context
import androidx.room.Room
import com.quitsmoking.data.dao.QuitRecordDao
import com.quitsmoking.data.dao.UserProfileDao
import com.quitsmoking.data.dao.HabitDao
import com.quitsmoking.data.dao.UserDao
import com.quitsmoking.data.database.ImNotDieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideImNotDieDatabase(@ApplicationContext context: Context): ImNotDieDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ImNotDieDatabase::class.java,
            "im_not_die_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideQuitRecordDao(database: ImNotDieDatabase): QuitRecordDao {
        return database.quitRecordDao()
    }
    
    @Provides
    fun provideUserProfileDao(database: ImNotDieDatabase): UserProfileDao {
        return database.userProfileDao()
    }
    
    @Provides
    fun provideHabitDao(database: ImNotDieDatabase): HabitDao {
        return database.habitDao()
    }
    
    @Provides
    fun provideUserDao(database: ImNotDieDatabase): UserDao {
        return database.userDao()
    }
}