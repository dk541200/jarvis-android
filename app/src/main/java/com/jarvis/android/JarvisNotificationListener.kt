package com.jarvis.android

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class JarvisNotificationListener:NotificationListenerService(){
    override fun onNotificationPosted(sbn:StatusBarNotification?){
        // Notification access is opt-in. This first build intentionally does not upload notification content.
    }
}
