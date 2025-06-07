package com.github.imhammer.saviotasks.dto

data class AuthResponseDTO(
    // Success parameters
    val token: String?,
    val isAdmin: Number?,
    val employeeName: String?,

    // Error parameter
    val message: String?
)