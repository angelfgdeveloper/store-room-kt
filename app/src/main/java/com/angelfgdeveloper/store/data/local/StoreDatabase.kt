package com.angelfgdeveloper.store.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.angelfgdeveloper.store.data.model.StoreEntity

@Database(entities = [StoreEntity::class], version = 2)
abstract class StoreDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao

    companion object {
        private var INSTANCE: StoreDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE StoreEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        fun getDatabase(context: Context): StoreDatabase {
            INSTANCE = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                StoreDatabase::class.java,
                "StoreDatabase"
            ).addMigrations(MIGRATION_1_2).build()
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}