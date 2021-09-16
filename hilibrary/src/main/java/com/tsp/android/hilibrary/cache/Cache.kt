package com.tsp.android.hilibrary.cache

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

/**
 *     author : shengping.tian
 *     time   : 2021/09/08
 *     desc   : 缓存类
 *     version: 1.0
 */

@Entity(tableName = "cache")
class Cache {

    @PrimaryKey(autoGenerate = false)
    @NotNull
    var key: String = ""

    var data: ByteArray? = null
}