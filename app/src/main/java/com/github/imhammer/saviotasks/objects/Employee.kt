package com.github.imhammer.saviotasks.objects

import com.github.imhammer.saviotasks.Utils
import com.github.imhammer.saviotasks.constants.EmployeeStatus
import com.github.imhammer.saviotasks.dto.EmployeeDTO

class Employee(private val dto: EmployeeDTO)
{
    private var status: EmployeeStatus = Utils.getEmployeeStatus(dto)
    private val tasks: MutableList<Task> = mutableListOf()

    fun getDTO() = dto

    fun getId() = dto.id
    fun getStoreId() = dto.store_id
    fun getStatus() = status
    fun getName() = dto.name
    fun getWorkStart() = dto.work_start
    fun getWorkEnd() = dto.work_end

    fun setStatus(employeeStatus: EmployeeStatus)
    {
        status = employeeStatus
    }

    fun addTask(task: Task)
    {
        this.tasks.add(task)
    }

    fun getTasks(): List<Task>
    {
        return this.tasks.toList()
    }
}