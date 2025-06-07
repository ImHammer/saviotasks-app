package com.github.imhammer.saviotasks.services

import com.github.imhammer.saviotasks.Utils.Companion.randomBetween
import com.github.imhammer.saviotasks.Utils.Companion.randomText
import com.github.imhammer.saviotasks.constants.TaskStatus
import com.github.imhammer.saviotasks.dto.EmployeeDTO
import com.github.imhammer.saviotasks.dto.TaskDTO

class Factory
{
    companion object
    {
        fun createRandomEmployee(): EmployeeDTO
        {
            val isWorking = randomBetween(0, 1)
            var onLeave   = randomBetween(0, 1)

            if (isWorking == 1) {
                onLeave = 0;
            }

            return EmployeeDTO(
                randomBetween(0, 10000),
                randomText(8),
                1,
                "13:00",
                "22:00",
                randomBetween(0, 3),
                null,
                randomText(10),
                randomText(5),
                onLeave,
                isWorking
            )
        }

        fun createRandomTask(employeeId: Int): TaskDTO
        {
            val status = when(randomBetween(0, 2)) {
                0 -> TaskStatus.COMPLETED
                1 -> TaskStatus.INPROGRESS
                2 -> TaskStatus.PENDING
                else -> {TaskStatus.PENDING}
            }

            return TaskDTO(
                id = randomBetween(0, 10000),
                storeId = randomBetween(1, 3),
                employee_id = employeeId,
                name = randomText(randomBetween(20, 50)),
                status = status.id
            )
        }
    }
}