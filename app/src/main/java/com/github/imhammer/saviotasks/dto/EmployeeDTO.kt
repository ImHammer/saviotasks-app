package com.github.imhammer.saviotasks.dto

import kotlinx.serialization.json.JsonNames

data class AlternativeSchedule(
    var work_start: String,
    var work_end: String,
)

data class EmployeeDTO(
    var id: Int,
    var name: String,
    var active: Int,
    var work_start: String,
    var work_end: String,
    var store_id: Int,
    var alternative_schedule: String?,
    var username: String,
    var store_name: String,
    var on_leave: Int,
    var is_working: Int,
    var deserialized_alt_sched: Map<Int, AlternativeSchedule>? = null
)
