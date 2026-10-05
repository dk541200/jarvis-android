package com.jarvis.android;

import android.app.*;
import android.content.*;
import android.os.*;
import androidx.core.app.NotificationCompat;

public class JarvisService extends Service {
    public static final String ACTION_ON="ON", ACTION_SLEEP="SLEEP", ACTION_OFF="OFF";
    private static final String CHANNEL="jarvis_core";

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(1, notification("🟢 JARVIS ACTIVE"));
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? ACTION_ON : intent.getAction();
        if (ACTION_SLEEP.equals(action)) update("💤 JARVIS SLEEPING");
        else if (ACTION_OFF.equals(action)) { stopForeground(true); stopSelf(); }
        else update("🟢 JARVIS ACTIVE");
        return START_STICKY;
    }

    private void update(String text) {
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1, notification(text));
    }

    private Notification notification(String text) {
        Intent on=new Intent(this,JarvisService.class).setAction(ACTION_ON);
        Intent sleep=new Intent(this,JarvisService.class).setAction(ACTION_SLEEP);
        Intent off=new Intent(this,JarvisService.class).setAction(ACTION_OFF);
        PendingIntent po=PendingIntent.getService(this,10,on,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent ps=PendingIntent.getService(this,11,sleep,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pf=PendingIntent.getService(this,12,off,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this,CHANNEL)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Jarvis")
            .setContentText(text)
            .setOngoing(true)
            .addAction(0,"ON",po)
            .addAction(0,"SLEEP",ps)
            .addAction(0,"OFF",pf)
            .build();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT>=26)
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE))
                .createNotificationChannel(new NotificationChannel(CHANNEL,"Jarvis Core",NotificationManager.IMPORTANCE_LOW));
    }

    @Override public IBinder onBind(Intent intent){ return null; }
}
