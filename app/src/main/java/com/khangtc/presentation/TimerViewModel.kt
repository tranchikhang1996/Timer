package com.khangtc.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khangtc.constants.ALARM_TIME_KEY
import com.khangtc.constants.TOTAL_TIME_KEY
import com.khangtc.data.DataPersistence
import com.khangtc.helper.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class TimerViewModel(private val dataPersistence: DataPersistence) : ViewModel() {
    private val _currentProcess = MutableLiveData<Pair<String, Float>>()
    val currentProcess: LiveData<Pair<String, Float>> = _currentProcess
    private val _timeOut = SingleLiveEvent<Unit>()
    val timeOut: LiveData<Unit> = _timeOut
    private val _scheduled = SingleLiveEvent<Pair<Long, Long>>()
    val scheduled: LiveData<Pair<Long, Long>> = _scheduled
    private val _savedTime = SingleLiveEvent<Pair<Long, Long>>()
    val savedTime: LiveData<Pair<Long, Long>> = _savedTime
    private val timeFormat = "%02d:%02d:%02d"
    private val updatedInterval = 200L
    private var currentTime = 0L
    private var totalTime = 0L
    private var job: Job? = null

    fun startCountDown(time: Long, totalTime: Long) {
        if (time <= 0 || totalTime <= 0) {
            finish()
            return
        }
        this.currentTime = time
        this.totalTime = totalTime
        job = viewModelScope.launch {
            while (currentTime > 0L) {
                delay(updatedInterval)
                countDown()
            }
            finish()
        }
    }

    private fun countDown() {
        currentTime -= updatedInterval
        _currentProcess.postValue(
            Pair(
                formatTime(currentTime / 1000L),
                currentTime.toFloat() / totalTime
            )
        )
    }

    private fun finish() {
        job?.cancel()
        job = null
        _timeOut.postValue(Unit)
    }

    fun start(time: Long) {
        if (time <= 0) {
            finish()
            return
        }
        _scheduled.postValue(Pair(time, time))
        startCountDown(time, time)
    }

    fun pause() {
        job?.cancel()
        job = null
    }

    fun resume() {
        _scheduled.postValue(Pair(currentTime, totalTime))
        startCountDown(currentTime, totalTime)
    }

    fun cancel() {
        job?.cancel()
        job = null
        currentTime = 0L
        totalTime = 0L
    }

    fun getRemainTime() {
        viewModelScope.launch(Dispatchers.IO) {
            val remainTime = dataPersistence.readLong(ALARM_TIME_KEY, 0L) - System.currentTimeMillis()
            val total = dataPersistence.readLong(TOTAL_TIME_KEY, 0L)
            _savedTime.postValue(Pair(remainTime, total))
        }
    }

    fun saveRemainTime(time: Long, total: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dataPersistence.writeLong(ALARM_TIME_KEY, time + System.currentTimeMillis())
            dataPersistence.writeLong(TOTAL_TIME_KEY, total)
        }
    }

    fun clearRemainTime() {
        viewModelScope.launch(Dispatchers.IO) {
            dataPersistence.remove(ALARM_TIME_KEY, TOTAL_TIME_KEY)
        }
    }

    private fun formatTime(time: Long): String {
        val hour = time / 3600L
        val minute = (time % 3600L) / 60L
        val second = (time % 3600L) % 60L
        return String.format(Locale.US, timeFormat, hour, minute, second)
    }
}