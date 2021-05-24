package com.nanioi.alarm_application

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)
        notifyNotification(context)
    }

    companion object{
        const val NOTIFICATION_CHANNEL_ID = "1000"
        const val NOTIFICATION_ID = 100
    }
    //채널만들기
    private fun createNotificationChannel(context: Context) {
        //버전이 낮으면 채널 임의로 만들어주기
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "기상 알람",
                    NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }
    private fun notifyNotification(context: Context) {
        with(NotificationManagerCompat.from(context)){
            val build = NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("알람")
                    .setContentText("일어날 시간입니다.")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

            notify(NOTIFICATION_ID,build.build())
        }
    }
}