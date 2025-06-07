package com.github.imhammer.saviotasks.objects

import com.github.imhammer.saviotasks.constants.TaskPriority

data class NotificationTask(
    val name: String?,
    val desc: String?,
    val priority: TaskPriority = TaskPriority.LOW
)
