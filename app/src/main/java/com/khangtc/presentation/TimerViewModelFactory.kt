package com.khangtc.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khangtc.data.SharedPrefDataPersistence

class TimerViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val dataPersistence = SharedPrefDataPersistence(context)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(dataPersistence) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}