package com.tsp.android.hilibrary.cache

import androidx.room.*

/**
 *     author : shengping.tian
 *     time   : 2021/09/08
 *     desc   : room 数据库 Dao
 *     version: 1.0
 */
@Dao
interface CacheDao {

    @Insert(entity = Cache::class, onConflict = OnConflictStrategy.REPLACE)
    fun saveCache(cache: Cache): Long

    @Query("select * from cache where `key`=:key")
    fun getCache(key: String): Cache?

    @Delete(entity = Cache::class)
    fun deleteCache(cache: Cache)

//    @Delete
//    fun deleteCache(key: String)
}