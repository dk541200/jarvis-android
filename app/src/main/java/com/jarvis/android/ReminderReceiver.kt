package com.jarvis.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver:BroadcastReceiver(){
    override fun onReceive(context:Context,intent:Intent?){
        val manager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=26) manager.createNotificationChannel(NotificationChannel("jarvis_reminders","JARVIS reminders",NotificationManager.IMPORTANCE_DEFAULT))
        val n=NotificationCompat.Builder(context,"jarvis_reminders")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("JARVIS reminder")
            .setContentText(intent?.getStringExtra("text")?:"You have a reminder")
            .setAutoCancel(true).build()
        manager.notify((System.currentTimeMillis()%100000).toInt(),n)
    }
}
