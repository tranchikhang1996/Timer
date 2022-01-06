package com.khangtc.data

interface DataPersistence {
    fun writeLong(key: String, value: Long)
    fun readLong(key: String, default: Long): Long
    fun remove(vararg keys: String)
}