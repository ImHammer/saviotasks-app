package com.github.imhammer.saviotasks.services

import android.media.RingtoneManager
import android.util.JsonReader
import android.util.Log
import com.github.imhammer.saviotasks.constants.TaskPriority
import com.github.imhammer.saviotasks.dto.FirebaseMessageDTO
import com.github.imhammer.saviotasks.objects.NotificationTask
import com.github.imhammer.saviotasks.services.adapters.TaskPriorityAdapter
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.StringReader


class FirebaseService() : FirebaseMessagingService()
{
    private var notificationService: NotificationService? = null
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(TaskPriority::class.java, TaskPriorityAdapter())
        .create()

    private var userManager: UserManager? = null

    override fun onMessageReceived(message: RemoteMessage)
    {
        super.onMessageReceived(message)

        userManager = UserManager(this)

        if (!(userManager!!.hasCredentials())) return
        if (!(userManager!!.isValid())) return

        if (notificationService == null) {
            notificationService = NotificationService(this)
            notificationService!!.createNotificationChannel()
        }

        if (notificationService != null) {
            message.data.let { data ->
                try {
                    val firebaseMessageDto = gson.fromJson(gson.toJson(data), FirebaseMessageDTO::class.java)

                    if (!firebaseMessageDto.isValid()) {
                        throw Exception("DTO is invalid")
                    }

                    if (firebaseMessageDto.toEmployeeId != userManager!!.getEmployeeId().toString()) {
                        throw Exception("Ignoring notification, employee ids do not match, received ${firebaseMessageDto.toEmployeeId} of expected ${userManager!!.getEmployeeId()}")
                    }

                    val type = object : TypeToken<List<NotificationTask>>() {}.type
                    val tasks: MutableList<NotificationTask> =
                        gson.fromJson(firebaseMessageDto.tasks, type)
                            ?: throw Exception("tasks cannot be null")

                    for (task in tasks) {
                        notificationService!!.sendOneTaskNotification(
                            firebaseMessageDto.toEmployee ?: "",
                            firebaseMessageDto.fromEmployee ?: "",
                            task
                        )
                    }

                    Log.d("FirebaseService", "Message: $firebaseMessageDto")
                } catch (e: Exception) {
                    Log.e("FirebaseService", "Processing message error", e)
                }
            }
        }
        super.onMessageReceived(message)
    }

    override fun onNewToken(token: String)
    {
        Log.d("FIREBASE SERVICE", "FCM TOKEN: ${token}")
        super.onNewToken(token)
    }
}