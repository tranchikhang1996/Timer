package com.khangtc.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.khangtc.R
import com.khangtc.constants.ALARM_REQUEST_CODE
import com.khangtc.presentation.actionstate.*
import com.khangtc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ActionController {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var timerViewModel: TimerViewModel
    private lateinit var actionState: ActionState
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getPreferences(MODE_PRIVATE)
        actionState = ActionStateFactory.getActionState(
            savedInstanceState?.getString(ActionState.TAG, "") ?: "", this
        )
        actionState.applyUI()
        initViewModel()
        binding.timePicker.apply { setIs24HourView(true) }
        binding.start.setOnClickListener { actionState.clickStart() }
        binding.cancel.setOnClickListener { actionState.clickCancel() }
    }

    override fun onStart() {
        super.onStart()
        timerViewModel.getRemainTime()
    }

    override fun onStop() {
        super.onStop()
        timerViewModel.pause()
    }

    override fun changeState(state: ActionState) {
        actionState = state
    }

    override fun setStartBackground(id: Int) {
        binding.start.setBackgroundResource(id)
    }

    override fun setStartText(id: Int, color: Int) {
        binding.start.setText(id)
        binding.start.setTextColor(ContextCompat.getColor(this, color))
    }

    override fun setCancelText(id: Int, color: Int) {
        binding.cancel.setText(id)
        binding.cancel.setTextColor(ContextCompat.getColor(this, color))
    }

    override fun setPickerVisible(isVisible: Boolean) {
        binding.timePicker.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setTimerVisible(isVisible: Boolean) {
        binding.timer.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun start() {
        val time = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.timePicker.hour * 3600L + binding.timePicker.minute * 60L
        } else {
            binding.timePicker.currentHour * 3600L + binding.timePicker.currentMinute * 60L
        }
        timerViewModel.start(time * 1000L)
    }

    override fun resumeCountDown() {
        timerViewModel.resume()
    }

    override fun pause() {
        timerViewModel.pause()
        removeTime()
    }

    override fun cancel() {
        timerViewModel.cancel()
        resetUI()
        removeTime()
    }

    private fun resetUI() {
        actionState = InitState(this).apply { applyUI() }
        binding.timer.setCurrent(0f)
        binding.timer.text = getString(R.string.default_time)
    }

    private fun initViewModel() {
        timerViewModel = ViewModelProvider(this, TimerViewModelFactory(applicationContext))[TimerViewModel::class.java]
        timerViewModel.currentProcess.observe(this, {
            binding.timer.text = it.first
            binding.timer.setCurrent(it.second)
        })
        timerViewModel.timeOut.observe(this, {
            resetUI()
        })

        timerViewModel.scheduled.observe(this, {
            prepareAlarm(it.first)
            timerViewModel.saveRemainTime(it.first, it.second)
        })

        timerViewModel.savedTime.observe(this, {
            if (it.first > 0) {
                actionState = ActionStateFactory.getActionState(RunningState.Tag, this)
                actionState.applyUI()
            }
            timerViewModel.startCountDown(it.first, it.second)
        })
    }

    private fun removeTime() {
        cancelAlarm()
        timerViewModel.clearRemainTime()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ActionState.TAG, actionState.tag)
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, it, 0)
        }
        alarmManager.cancel(intent)
    }

    private fun prepareAlarm(time: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, it, 0)
        }
        val alarmTimeAtUTC = System.currentTimeMillis() + time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeAtUTC,
                    intent
                )
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, intent)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, intent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, intent)
        }
    }
}