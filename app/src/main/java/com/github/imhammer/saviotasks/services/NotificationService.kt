package com.github.imhammer.saviotasks.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.imhammer.saviotasks.MainActivity
import com.github.imhammer.saviotasks.R
import com.github.imhammer.saviotasks.objects.NotificationTask

class NotificationService(private val context: Context)
{
    private var CHANNEL_ID: String

    init {
        CHANNEL_ID = context.getString(R.string.channel_id)
    }

    fun createNotificationChannel()
    {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.apply {
            description = descriptionText
            enableLights(true)
            enableVibration(true)
            setShowBadge(true)
            setSound(alarmSound, AudioAttributes.Builder().build())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setAllowBubbles(true)
            }
        }

        // Register the channel with the system.
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(CHANNEL_ID)
        notificationManager.createNotificationChannel(channel)
    }

    fun sendOneTaskNotification(toEmployeeName: String, fromEmployeeName: String, notificationTask: NotificationTask)
    {
        val (name, desc, priority) = notificationTask

        val notificationManager = NotificationManagerCompat.from(context)
        if (notificationManager.areNotificationsEnabled()) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val inboxStyle = NotificationCompat.InboxStyle()
                .addLine("Tarefa: $name")
                .addLine("Prioridade: ${priority.desc}")

            if (desc != null && desc.isNotEmpty() && desc.isNotBlank()) {
                inboxStyle.addLine(desc)
            }

            var notBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("${toEmployeeName}, você tem uma nova tarefa na Savio")
                .setContentText("$fromEmployeeName, criou uma nova tarefa que foi redirecionada a você.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setStyle(inboxStyle)

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            notBuilder.setSound(alarmSound)

            notificationManager.notify(System.currentTimeMillis().toInt(), notBuilder.build())
        }
    }
}