package com.khangtc.data

import android.content.Context

class SharedPrefDataPersistence(context: Context) : DataPersistence {
    private val sharedRef = context.getSharedPreferences("timer_sharePref", Context.MODE_PRIVATE)
    override fun writeLong(key: String, value: Long) {
        with(sharedRef.edit()) {
            putLong(key, value)
            commit()
        }
    }

    override fun readLong(key: String, default: Long) = sharedRef.getLong(key, default)
    override fun remove(vararg keys: String) {
        with(sharedRef.edit()) {
            for(key in keys) {
                this.remove(key)
            }
            commit()
        }
    }
}