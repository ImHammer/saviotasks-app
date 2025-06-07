package com.github.imhammer.saviotasks.dto

data class DistributeDTO (
    var date: String? = null,
    var debug: Debug? = null,
    var store_id: Int? = null
)

data class Debug(
    var currentDate: String? = null,
    var totalTaskCount: Int? = null,
    var unassignedTaskCount: Int? = null
)