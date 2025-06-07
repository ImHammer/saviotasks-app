package com.github.imhammer.saviotasks.services

import android.content.Context
import android.util.Log
import com.github.imhammer.saviotasks.MainActivity
import com.github.imhammer.saviotasks.Utils
import com.github.imhammer.saviotasks.constants.DayOfWeek
import com.github.imhammer.saviotasks.constants.EmployeeStatus
import com.github.imhammer.saviotasks.constants.TaskStatus
import com.github.imhammer.saviotasks.dto.AlternativeSchedule
import com.github.imhammer.saviotasks.dto.AuthResponseDTO
import com.github.imhammer.saviotasks.dto.ChangeStatusDTO
import com.github.imhammer.saviotasks.dto.CreateTaskDTO
import com.github.imhammer.saviotasks.dto.Debug
import com.github.imhammer.saviotasks.dto.DistributeDTO
import com.github.imhammer.saviotasks.dto.EmployeeDTO
import com.github.imhammer.saviotasks.dto.FcmTokenDTO
import com.github.imhammer.saviotasks.dto.GenericResponseMessage
import com.github.imhammer.saviotasks.dto.StoreDTO
import com.github.imhammer.saviotasks.dto.TaskDTO
import com.github.imhammer.saviotasks.objects.Employee
import com.github.imhammer.saviotasks.objects.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ApiService(context: Context, retrofit: Retrofit, private val repository: SavioApiRepository)
{
    private val gson = Gson()

    private val converter = retrofit.responseBodyConverter<GenericResponseMessage>(GenericResponseMessage::class.java, arrayOfNulls(0))

//    private var filterDate: String = Utils.getActualDateFormatted("yyyy-MM-dd");
    private var filterDate: Long = System.currentTimeMillis()
    private var filterTaskStatus: TaskStatus = TaskStatus.ALL

    private var cacheAvailableEmployees: List<EmployeeDTO>? = null
    private var cacheAllEmployees: List<EmployeeDTO>? = null
    private var cacheTasks: List<TaskDTO>? = null

    private var cacheFcmToken: String? = null
    private var lastErrorMessage = MutableStateFlow<GenericResponseMessage?>(null)
    var externaleState: StateFlow<GenericResponseMessage?> = lastErrorMessage

    fun getLastError(): GenericResponseMessage?
    {
        return lastErrorMessage.value
    }

    fun setDateFilter(millis: Long)
    {
        if (millis != filterDate) {
            filterDate = millis

            cacheAvailableEmployees = null
            cacheTasks = null
        }
    }

    fun setTaskStatusFilter(taskStatus: TaskStatus = TaskStatus.ALL)
    {
        if (taskStatus != filterTaskStatus) {
            filterTaskStatus = taskStatus

            cacheTasks = null
        }
    }

    fun getDateFilterFormatted(): String
    {
        return Utils.getMillisFormatted(filterDate, "yyyy-MM-dd")
    }

    suspend fun getTasks(): List<TaskDTO>?
    {
        if (cacheTasks != null) {
            return cacheTasks
        }

        if (MainActivity.getUserManager().isValid()) {
            val status: String? = if (filterTaskStatus == TaskStatus.ALL) null else filterTaskStatus.id
            val response = try {
                repository.readTasks(
                    MainActivity.getUserManager().getTokenToRequest() ?: "",
                    MainActivity.getUserManager().getStoreId().toString(),
                    getDateFilterFormatted(),
                    status
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // error("ApiService->getTasks(): Error on request")
                null
            }

            if (response != null) {
                if (response.isSuccessful) {
                    cacheTasks = response.body()
                    return cacheTasks
                } else {
                    response.errorBody()?.let { errorBody ->
                        parseErrorBody(errorBody)?.let {
                            it.code = response.code()
                            lastErrorMessage.value = it
                        }
                    }
                }
            }
        }

        return null
    }

    suspend fun getAllEmployees(): List<EmployeeDTO>?
    {
        // EVITANDO FAZER UM NOVO REQUEST PARA O SERVIDOR POR SER DESNECESSÁRIO
        if (cacheAllEmployees != null) {
            return cacheAllEmployees
        }

        if (MainActivity.getUserManager().isValid()) {
            val response = try {
                repository.readEmployees(
                    MainActivity.getUserManager().getTokenToRequest() ?: ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (response != null) {
                if (response.isSuccessful) {
                    cacheAllEmployees = response.body()

                    val now = LocalTime.now()
                    val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    for (emp in cacheAllEmployees!!) {
                        emp.deserialized_alt_sched = emp.alternative_schedule?.let {
                            gson.fromJson(it, object : TypeToken<Map<Int, AlternativeSchedule>>() {}.type)
                        }

                        if (emp.deserialized_alt_sched != null) {
                            val nameOfDayWeek = Utils.getDayOfWeekName(filterDate)
                            val dayOfWeekFilter = DayOfWeek.ofText(nameOfDayWeek)
                            if (dayOfWeekFilter != null) {
                                if (emp.deserialized_alt_sched!!.containsKey(dayOfWeekFilter.id)) {
                                    emp.work_start = emp.deserialized_alt_sched!![dayOfWeekFilter.id]?.work_start ?: emp.work_start
                                    emp.work_end = emp.deserialized_alt_sched!![dayOfWeekFilter.id]?.work_end ?: emp.work_end
                                }
                            }
                        }

                        val workStart = LocalTime.parse(emp.work_start, formatter)
                        val workEnd = LocalTime.parse(emp.work_end, formatter)

                        emp.is_working = if (now.isAfter(workStart) && now.isBefore(workEnd) && emp.on_leave != 1) 1 else 0

                        Utils.printObjectAsMap(emp)
                    }

                    return response.body()
                } else {
                    response.errorBody()?.let { errorBody ->
                        parseErrorBody(errorBody)?.let {
                            it.code = response.code()
                            lastErrorMessage.value = it
                        }
                    }
                }
            }
        }

        return null;
    }

    suspend fun getAvailableEmployees(): List<EmployeeDTO>?
    {
        if (cacheAvailableEmployees != null) {
            return cacheAvailableEmployees
        }

        if (MainActivity.getUserManager().isValid()) {
            val employees = getAllEmployees()
            val response = try {
                repository.readAvailableEmployees(
                    MainActivity.getUserManager().getTokenToRequest() ?: "",
                    getDateFilterFormatted(),
                    MainActivity.getUserManager().getStoreId().toString()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (response != null) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    response.body()?.forEach { avaEmp ->
                        employees?.forEach { emp ->
                            run {
                                if (emp.id == avaEmp.id) {
                                    emp.on_leave = avaEmp.on_leave
                                }
                            }
                        }
                    }

//                    val availableListIds = response.body()?.map { emp -> emp.id }
                    cacheAvailableEmployees = employees
                    return cacheAvailableEmployees
                } else {
                    response.errorBody()?.let { errorBody ->
                        parseErrorBody(errorBody)?.let {
                            it.code = response.code()
                            lastErrorMessage.value = it
                        }
                    }
                }
            }
        }

        return null;
    }

    suspend fun createTask(task: Task): Boolean
    {
        if (MainActivity.getUserManager().isValid()) {
            val response = try {
                repository.createTask(
                    MainActivity.getUserManager().getTokenToRequest() ?: "",
                    CreateTaskDTO(
                        Utils.getActualDateFormatted("yyyy-MM-dd"),
                        task.getDescription(),
                        task.getEmployeeId(),
                        task.isFixed(),
                        task.getName(),
                        task.getPriority().id,
                        MainActivity.getUserManager().getStoreId()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // error("ApiService->createTask(): Error on request")
                null
            }

            if (response != null) {
                if (response.isSuccessful) {
                    return true
                } else {
                    response.errorBody()?.let { errorBody ->
                        parseErrorBody(errorBody)?.let {
                            it.code = response.code()
                            lastErrorMessage.value = it
                        }
                    }
                }
            }
        }
        return false
    }

    suspend fun distributeTasks(tasksAmount: Int): Boolean
    {
        if (MainActivity.getUserManager().isValid()) {
            val response = try {
                repository.distributeTasks(
                    MainActivity.getUserManager().getTokenToRequest() ?: "",
                    DistributeDTO(
                        Utils.getActualDateFormatted("yyyy-MM-dd"),
                        Debug(
                            Utils.getActualDateFormatted("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
                            tasksAmount,
                            0
                        ),
                        MainActivity.getUserManager().getStoreId()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // error("ApiService->distributeTasks(): Error on request")
                null
            }

            if (response != null) {
                if (response.isSuccessful) {
                    return true
                } else {
                    response.errorBody()?.let { errorBody ->
                        parseErrorBody(errorBody)?.let {
                            it.code = response.code()
                            lastErrorMessage.value = it
                        }
                    }
                }
            }
        }
        return false
    }

    fun clearCaches()
    {
        cacheAllEmployees = null
        cacheAvailableEmployees = null
        cacheTasks = null
    }

    suspend fun findUserById(): EmployeeDTO?
    {
        if (MainActivity.getUserManager().isValid()) {
            val employees = getAllEmployees()
            if (employees != null) {
                val userId = MainActivity.getUserManager().getEmployeeId()

                for (employee in employees) {
                    if (employee.id == userId) {
                        return employee
                    }
                }
            }
        }
        return null
    }

    suspend fun sendFCMToken(token: String): Boolean
    {
        if (MainActivity.getUserManager().isValid()) {
            val response = try {
                repository.sendFCMToken(
                    MainActivity.getUserManager().getTokenToRequest() ?: "",
                    FcmTokenDTO(
                        MainActivity.getUserManager().getId(),
                        token
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // error("ApiService->sendFCMToken(): Error on request")
                null
            }

            if (response != null) {
                if (response.isSuccessful) {
                    return true
                } else {
                    response.errorBody()?.let { errorBody ->
                        parseErrorBody(errorBody)?.let {
                            it.code = response.code()
                            lastErrorMessage.value = it
                        }
                    }
                }
            }
        }
        MainActivity.getUserManager().saveFcmToken(token)
        return false
    }

    suspend fun changeTaskStatus(task: Task, taskStatus: TaskStatus): Boolean
    {
        if (MainActivity.getUserManager().isValid()) {
            val response = repository.changeTaskStatus(
                MainActivity.getUserManager().getTokenToRequest() ?: "",
                task.getId(),
                ChangeStatusDTO(
                    taskStatus.id,
                    Utils.getActualDateFormatted("yyyy-MM-dd")
                )
            )
            if (response.isSuccessful) {
                return true
            } else {
                response.errorBody()?.let { errorBody ->
                    parseErrorBody(errorBody)?.let {
                        it.code = response.code()
                        lastErrorMessage.value = it
                    }
                }
                return false
            }
        }
        return false
    }

    suspend fun parseErrorBody(errorBody: ResponseBody): GenericResponseMessage?
    {
        return try {
            converter.convert(errorBody)
        } catch (e: Exception) {
            null
        }
    }
}