package com.tsp.android.hilibrary.cache

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tsp.android.hilibrary.utils.AppGlobals

/**
 *     author : shengping.tian
 *     time   : 2021/09/08
 *     desc   :
 *     version: 1.0
 */
@Database(entities = [Cache::class], version = 1)
abstract class CacheDatabase : RoomDatabase() {

    companion object {
        private var database: CacheDatabase
        fun get(): CacheDatabase {
            return database
        }

        init {
            val context = AppGlobals.get()!!.applicationContext
            database = Room.databaseBuilder(context, CacheDatabase::class.java, "jgs_cache").build()
        }
    }

    abstract val cacheDao: CacheDao

}