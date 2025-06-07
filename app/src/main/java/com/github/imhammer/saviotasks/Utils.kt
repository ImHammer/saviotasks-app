package com.github.imhammer.saviotasks

import android.util.Log
import com.github.imhammer.saviotasks.constants.EmployeeStatus
import com.github.imhammer.saviotasks.dto.EmployeeDTO
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class Utils
{
    companion object
    {
        fun getActualDateFormatted(format: String): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))

        fun getMillisFormatted(millis: Long, format: String): String
        {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(Date(millis))
        }

        fun getDayOfWeekName(millis: Long): String
        {
            val instant = Instant.ofEpochMilli(millis)
            val today = instant.atZone(ZoneId.systemDefault()).toLocalDate()
            return today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US)
        }

        fun randomBetween(min: Int, max: Int): Int {
            return Random.nextInt(min, max + 1)
        }

        fun randomText(length: Int): String
        {
            val characteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ "
            var randomText = ""

            for (i in 0..length) {
                randomText += characteres[randomBetween(0, characteres.length - 1)]
            }
            return randomText
        }

        fun getEmployeeStatus(employee: EmployeeDTO): EmployeeStatus
        {
            return if (employee.on_leave == 1) {
                EmployeeStatus.DAYOFF
            } else if (employee.is_working == 1) {
                EmployeeStatus.AVAILABLE
            } else {
                EmployeeStatus.INOFF
            }
        }

        fun filterEmployeesNotDone(list: List<EmployeeDTO>): List<EmployeeDTO>
        {
            return list.filter { getEmployeeStatus(it) != EmployeeStatus.AVAILABLE }
        }

        // Função que transforma um objeto em Map<String, Any?>
        fun toMap(obj: Any): Map<String, Any?> {
            return obj::class.memberProperties.associate { property ->
                // Usa 'safe cast' para evitar problemas de tipo
                property.name to (property as? KProperty1<Any, *>?)?.getter?.call(obj)
            }
        }

        fun printObjectAsMap(obj: Any) {
            val map = toMap(obj)

            Log.i("MAPOBJECT", "==============")
            map.forEach { (key, value) ->
                Log.i("MAPOBJECT", "$key: $value")
            }
            Log.i("MAPOBJECT", "==============")
        }
    }
}