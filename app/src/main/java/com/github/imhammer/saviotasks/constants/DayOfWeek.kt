package com.github.imhammer.saviotasks.constants

enum class DayOfWeek(val id: Int, val text: String)
{
    MONDAY(1, "Monday"),
    TUESDAY(2, "Tuesday"),
    WEDNESDAY(3, "Wednesday"),
    THURSDAY(4, "Thursday"),
    FRIDAY(5, "Friday"),
    SATURDAY(6, "Saturday"),
    SUNDAY(7, "Sunday");

    companion object {
        fun ofId(id: Int): DayOfWeek?
        {
            for (dow in entries) {
                if (dow.id == id) return dow
            }
            return null
        }
        fun ofText(text: String): DayOfWeek?
        {
            for (dow in entries) {
                if (dow.text.lowercase().equals(text.lowercase())) return dow
            }
            return null
        }
    }
}