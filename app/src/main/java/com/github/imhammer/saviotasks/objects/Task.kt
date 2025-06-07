package com.github.imhammer.saviotasks.objects

import com.github.imhammer.saviotasks.constants.TaskPriority
import com.github.imhammer.saviotasks.constants.TaskStatus
import com.github.imhammer.saviotasks.dto.TaskDTO

class Task(private val taskDTO: TaskDTO)
{
    fun getId() = taskDTO.id ?: -1
    fun getEmployeeId() = taskDTO.employee_id
    fun getText() = taskDTO.name
    fun getStatus() = TaskStatus.of(taskDTO.status)
    fun getDescription() = taskDTO.description
    fun getPriority() = TaskPriority.of(taskDTO.priority ?: 1) ?: TaskPriority.LOW

    fun getName() = getText()


    fun isFixed() = taskDTO.is_fixed == 1

    fun setStatus(taskStatus: TaskStatus)
    {
        taskDTO.status = taskStatus.id
    }
}