package com.bucic.radarisha.di.module

import android.content.Context
import androidx.room.Room
import com.bucic.data.database.user.MIGRATION_1_2
import com.bucic.data.database.user.UserDatabase
import com.bucic.data.database.user.dao.UserDao
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
    fun provideDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user.db"
        ).addMigrations(MIGRATION_1_2)
            .build()
    }


    @Provides
    fun provideUserDao(userDatabase: UserDatabase): UserDao {
        return userDatabase.userDao()
    }
}