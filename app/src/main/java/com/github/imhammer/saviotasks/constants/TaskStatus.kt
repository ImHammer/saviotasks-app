package com.github.imhammer.saviotasks.constants

enum class TaskStatus(val id: String, val desc: String)
{
    ALL("all", "Todos"),
    PENDING("pendente", "Pendente"),
    INPROGRESS("em-andamento", "Em Andamento"),
    COMPLETED("concluido", "Concluido");

    companion object {
        fun of(id: String): TaskStatus
        {
            return when(id) {
                PENDING.id -> PENDING
                INPROGRESS.id -> INPROGRESS
                COMPLETED.id -> COMPLETED
                else -> ALL
            }
        }
    }
}

