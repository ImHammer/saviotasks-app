package com.github.imhammer.saviotasks.dto

import com.github.imhammer.saviotasks.objects.NotificationTask

data class FirebaseMessageDTO(
    val fromEmployee: String?,
    val toEmployee: String?,
    val toEmployeeId: String?,
    val time: String?,
    val tasks: String?
) {
    fun isValid(): Boolean
    {
        return fromEmployee != null &&
                toEmployee != null &&
                toEmployeeId != null &&
                time != null &&
                tasks != null
    }
}