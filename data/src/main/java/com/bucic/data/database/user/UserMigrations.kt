package com.bucic.data.database.user

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE registered_user ADD COLUMN stayLoggedIn INTEGER NOT NULL DEFAULT 0")
    }
}