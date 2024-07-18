package com.bucic.data.database.user

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bucic.data.database.user.dao.UserDao
import com.bucic.data.entities.user.UserDbData

@Database(
    entities = [UserDbData::class],
    version = 2,
    exportSchema = false
)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}