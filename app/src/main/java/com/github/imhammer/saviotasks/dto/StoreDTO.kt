package com.github.imhammer.saviotasks.dto

data class StoreDTO(
    val id: Int? = null,
    val name: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val active: Int? = null, // Boolean
    val createdAt: String? = null
)
