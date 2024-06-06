package com.bucic.data.database.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bucic.data.entities.user.UserDbData

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserDbData)

    @Query("SELECT * FROM registered_user LIMIT 1")
    suspend fun getRegisteredUser(): UserDbData?

    @Query("DELETE FROM registered_user")
    suspend fun deleteAll()
}