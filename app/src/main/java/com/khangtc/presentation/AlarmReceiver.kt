package com.khangtc.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.khangtc.helper.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showNotification(context)
    }
}