package com.github.imhammer.saviotasks.views

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.imhammer.saviotasks.MainActivity
import com.github.imhammer.saviotasks.Utils
import com.github.imhammer.saviotasks.constants.EmployeeStatus
import com.github.imhammer.saviotasks.constants.NotifyStatus
import com.github.imhammer.saviotasks.constants.TaskStatus
import com.github.imhammer.saviotasks.dto.TaskDTO
import com.github.imhammer.saviotasks.objects.Employee
import com.github.imhammer.saviotasks.objects.Notification
import com.github.imhammer.saviotasks.objects.Task
import com.github.imhammer.saviotasks.services.Factory
import kotlinx.coroutines.launch

class AppScreenViewModel() : ViewModel()
{
    var notificationsList = mutableStateListOf<Notification>()

    var actualHourFormatted = Utils.getActualDateFormatted("HH:mm:ss");

    var employeeNameTitle by mutableStateOf(MainActivity.getUserManager().getName())
    var employeeStoreName by mutableStateOf(MainActivity.getUserManager().getStoreName())

    var showBottomSheet by mutableStateOf(false)
    var showDatePicker by mutableStateOf(false)
    var showCreateNewTask by mutableStateOf(false)

    var selectedStatusFilter by mutableStateOf<TaskStatus>(TaskStatus.ALL)
    var selectedDateFilter by mutableLongStateOf(System.currentTimeMillis())

    var tasksAmount by mutableIntStateOf(0)
    var tasksCompleted by mutableIntStateOf(0)

    var availableEmployees by mutableStateOf<List<Employee>>(listOf());
    var createdTaskslist = mutableStateListOf<TaskDTO>()

    fun handleCreateTask(taskDTO: TaskDTO)
    {
        viewModelScope.launch {
            if (MainActivity.getApiService().createTask(Task(taskDTO))) {
                addNotification("Tarefa criada com sucesso", NotifyStatus.SUCCESS)
            } else {
                addNotification("Não foi possível criar a tarefa", NotifyStatus.DANGER)

                MainActivity.getApiService().getLastError()?.let { error ->
                    addNotification(error.message ?: "Error message not found", NotifyStatus.DANGER)
                }
            }
        }
    }

    fun handleDistributeTasks()
    {
        viewModelScope.launch {
            if (MainActivity.getApiService().distributeTasks(tasksAmount)) {
                MainActivity.getApiService().clearCaches()
                addNotification("Tarefas distribuidas", NotifyStatus.SUCCESS)

                loadEmployees()
            } else {
                MainActivity.getApiService().getLastError()?.let {
                    addNotification(it.message ?: "Não foi possivel distribuir as tarefas", NotifyStatus.DANGER)
                }
            }
        }
    }

    fun getRealTasksAmount(): Int
    {
        return tasksAmount + createdTaskslist.size
    }

    fun addNotification(message: String, notifyStatus: NotifyStatus)
    {
        notificationsList.add(Notification(message, notifyStatus.color))
    }

    fun changeTask(task: Task, taskStatus: TaskStatus)
    {
        viewModelScope.launch {
            if (MainActivity.getApiService().changeTaskStatus(task, taskStatus)) {
                addNotification("Status da Tarefa Alterado", NotifyStatus.SUCCESS)
                task.setStatus(taskStatus)
            } else {
                addNotification("Não foi possivel alterar o status", NotifyStatus.DANGER)
                MainActivity.getApiService().getLastError()?.let {
                    addNotification(it.message ?: "Não foi possivel alterar o status", NotifyStatus.DANGER)
                }
            }
            loadEmployees(true)
        }
    }

    fun loadEmployees(recache: Boolean = false)
    {
        if (!MainActivity.getUserManager().isValid()) {
            addNotification("Erro na sesão de login, reinicie o app", NotifyStatus.DANGER)
            MainActivity.getUserManager().clearCredentials()
            return
        }

        MainActivity.getApiService().setDateFilter(selectedDateFilter)
        MainActivity.getApiService().setTaskStatusFilter(selectedStatusFilter)

        if (recache) {
            MainActivity.getApiService().clearCaches()
        }

        viewModelScope.launch {
            val employees = MainActivity.getApiService().getAvailableEmployees()
            if (employees == null) {
                addNotification("Error N 1000", NotifyStatus.DANGER)
                return@launch
            }

            val tasks = MainActivity.getApiService().getTasks()
            if (tasks == null) {
                addNotification("Error N 1001", NotifyStatus.DANGER)
                return@launch
            }

            var tasksCompletedAmount = 0
            val employeesList: List<Employee> = employees.map { empDto -> Employee(empDto) }
            for (employee in employeesList) {
                for (taskDto in tasks) {
                    val task = Task(taskDto)
                    if (task.getEmployeeId() != null && task.getEmployeeId() == employee.getId()) {
                        employee.addTask(task)
                    }
                }
                tasksCompletedAmount += employee.getTasks().filter {
                    empTask -> empTask.getStatus() == TaskStatus.COMPLETED
                }.size
            }

            tasksCompleted = tasksCompletedAmount
            tasksAmount = tasks.size
            availableEmployees = employeesList.sortedBy {
                if (it.getId() == MainActivity.getUserManager().getId()) 0 else 1
            }
        }
    }
}

