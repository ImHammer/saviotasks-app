package com.github.imhammer.saviotasks.constants

enum class TaskPriority(
    val id: Int,
    val desc: String
) {
    LOW(1, "Baixa"),
    MODERATE(2, "Média"),
    HIGH(3, "Alta");

    companion object {
        fun of(id: Int): TaskPriority?
        {
            for (task in entries) {
                if (task.id == id) return task
            }
            return null
        }
    }
}