package com.github.imhammer.saviotasks.dto

data class CreateTaskDTO (
    var date: String? = null,
    var description: String? = null,
    var employeeId: Int? = null,
    var isFixed: Boolean? = null,
    var name: String? = null,
    var priority: Int? = null,
    var store_id: Int? = null
)