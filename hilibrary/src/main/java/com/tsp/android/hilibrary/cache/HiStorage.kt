package com.tsp.android.hilibrary.cache

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 *     author : shengping.tian
 *     time   : 2021/09/08
 *     desc   : 缓存工具类
 *     version: 1.0
 */
object HiStorage {

    fun <T> saveCache(key: String, body: T) {
        val cache = Cache()
        cache.key = key
        cache.data = toByteArray(body)
        CacheDatabase.get().cacheDao.saveCache(cache)
    }

    /**
     * 获取缓存
     */
    fun <T> getCache(key: String): T? {
        val cache = CacheDatabase.get().cacheDao.getCache(key)
        return (if (cache?.data != null) {
            toObject(cache!!.data)
        } else null) as T
    }

    /**
     * 删除缓存
     */
    fun deleteCache(key: String) {
        val cache = Cache()
        cache.key = key
        CacheDatabase.get().cacheDao.deleteCache(cache)
    }

    /**
     * 将 object 类转成二进制数据进行存储
     */
    private fun <T> toByteArray(body: T): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            oos = ObjectOutputStream(baos)
            oos.writeObject(body)
            oos.flush()
            return baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            baos?.close()
            oos?.close()
        }
        return ByteArray(0)
    }

    /**
     * 将 cache 转换成 object
     */
    private fun toObject(data: ByteArray?): Any? {
        var bas: ByteArrayInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            bas = ByteArrayInputStream(data)
            ois = ObjectInputStream(bas)
            return ois.readObject()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bas?.close()
            ois?.close()
        }
        return null
    }
}