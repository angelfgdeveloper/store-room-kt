package com.angelfgdeveloper.store.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.angelfgdeveloper.store.data.model.StoreEntity

@Database(entities = [StoreEntity::class], version = 1)
abstract class StoreDatabase: RoomDatabase() {
    abstract fun storeDao(): StoreDao

    companion object {
        private var INSTANCE: StoreDatabase? = null

        fun getDatabase(context: Context): StoreDatabase {
            INSTANCE = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                StoreDatabase::class.java,
                "StoreDatabase"
            ).build()
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}