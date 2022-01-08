package com.khangtc

import android.app.Application
import com.khangtc.helper.NotificationHelper

class TimerApplication() : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(applicationContext)
    }
}