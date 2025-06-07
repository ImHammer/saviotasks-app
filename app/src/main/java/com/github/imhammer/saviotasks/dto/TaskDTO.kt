package com.github.imhammer.saviotasks.dto

data class TaskDTO(
    var id: Int? = null,
    var name: String = "Default",
    var employee_id: Int? = null,
    var date: String? = null,
    var is_fixed: Int = 0, // Boolean,
    var status: String = "inprogress",
    var priority: Int = 0, // Boolean,
    var storeId: Int? = null,
    var description: String? = null,
    var employee_name: String? = null,
    var store_name: String? = null
)