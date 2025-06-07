package com.github.imhammer.saviotasks.pages

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.sharp.ExitToApp
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.github.imhammer.saviotasks.AuthRoute
import com.github.imhammer.saviotasks.MainActivity
import com.github.imhammer.saviotasks.Utils
import com.github.imhammer.saviotasks.components.EmployeeCard
import com.github.imhammer.saviotasks.components.InputField
import com.github.imhammer.saviotasks.components.OptionItem
import com.github.imhammer.saviotasks.components.OptionsList
import com.github.imhammer.saviotasks.components.RoundedButton
import com.github.imhammer.saviotasks.constants.EmployeeStatus
import com.github.imhammer.saviotasks.constants.TaskPriority
import com.github.imhammer.saviotasks.constants.TaskStatus
import com.github.imhammer.saviotasks.dto.TaskDTO
import com.github.imhammer.saviotasks.objects.Employee
import com.github.imhammer.saviotasks.objects.Task
import com.github.imhammer.saviotasks.ui.theme.MyColors
import com.github.imhammer.saviotasks.ui.theme.SavioTasksTheme
import com.github.imhammer.saviotasks.views.AppScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.streams.toList
import kotlin.time.Duration

@Composable
fun ActualTime()
{
    var actualDateFormatted by remember { mutableStateOf(Utils.getActualDateFormatted("dd/MM/yyyy")) };
    var actualHourFormatted by remember { mutableStateOf(Utils.getActualDateFormatted("HH:mm:ss")) }
//            val actualHourFormatted = Utils.getActualDateFormatted("HH:mm:ss");

    LaunchedEffect(Unit)
    {
        while(true) {
            delay(1000)
            actualDateFormatted = Utils.getActualDateFormatted("dd/MM/yyyy")
            actualHourFormatted = Utils.getActualDateFormatted("HH:mm:ss")
        }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(actualDateFormatted, style = MaterialTheme.typography.bodyMedium)
        Text(actualHourFormatted, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(navController: NavController?)
{
    var viewModel = remember { AppScreenViewModel() }
    var scope = rememberCoroutineScope()
    var datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit)
    {
        while(true) {
            delay(10000)
            viewModel.loadEmployees(true)
            Log.i("APPPAGE", "RELOAD EMPLOYEES")
        }
    }

    LaunchedEffect(null, viewModel.selectedDateFilter, viewModel.selectedStatusFilter)
    {
        Log.i("APPPAGE", "Changed Filter")
        viewModel.loadEmployees(true)
    }

    LaunchedEffect(datePickerState.selectedDateMillis)
    {
        val millis = datePickerState.selectedDateMillis
        if (millis != null) {
            viewModel.selectedDateFilter = millis + (86400000L) // CORREÇÃO FEIA, MAS FUNCIONA :)
            viewModel.showDatePicker = false
        }
    }

    LaunchedEffect(viewModel.notificationsList.size)
    {
        delay(Duration.parse("2s"))
        viewModel.notificationsList.removeFirstOrNull()
    }

    if (viewModel.showCreateNewTask) {
        NewTaskDialog (
            availableEmployees = viewModel.availableEmployees.filter { emp -> emp.getStatus() == EmployeeStatus.AVAILABLE }
        ) {
            if (it != null) {
                viewModel.handleCreateTask(it)
            }
            viewModel.showCreateNewTask = false
        }
    }

    //// MODAL PARA NOTIFICAÇÕES
    if (viewModel.notificationsList.size > 0) {
        Popup(
            onDismissRequest = { viewModel.notificationsList.clear() },
            alignment = Alignment.TopStart
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MyColors.Gray
                )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 30.dp, horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    items(viewModel.notificationsList.size) { notfyIndex ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(viewModel.notificationsList[notfyIndex].color)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = viewModel.notificationsList[notfyIndex].text,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    //// MODAL PARA SELEÇÃO DO FILTRO DA DATA
    if (viewModel.showDatePicker) {
        Popup(
            onDismissRequest = { viewModel.showDatePicker = false },
            alignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .background(MyColors.DarkGray)
                    .padding(16.dp)
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        dayContentColor = MyColors.White,
                        disabledDayContentColor = MyColors.LightGray,
                        weekdayContentColor = MyColors.LightGray,
                        selectedYearContainerColor = MyColors.BlueSky,
                        selectedDayContainerColor = MyColors.BlueSky,
                        todayContentColor = MyColors.White,
                        todayDateBorderColor = MyColors.BlueSky,
                        headlineContentColor = MyColors.Gray,
                        navigationContentColor = MyColors.BlueSky
                    )
                )
            }
        }
    }

    //// POPUP PARA A SELEÇÃO DO STATUS DA TAREFA PARA O FILTER
    if (viewModel.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.showBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = MyColors.Gray,
            contentColor = MyColors.White
        ) {
            OptionsList(
                options = listOf(
                    OptionItem("Todos", MyColors.LightGray, TaskStatus.ALL),
                    OptionItem("Pendente", MyColors.Yellow, TaskStatus.PENDING),
                    OptionItem("Em Andamento", MyColors.DarkGray, TaskStatus.INPROGRESS),
                    OptionItem("Concluido", MyColors.Lime, TaskStatus.COMPLETED)
                )
            ) {
                viewModel.selectedStatusFilter = it.data
                viewModel.showBottomSheet = false
            }
        }
    }

    Scaffold(
        containerColor = Color.Black,
        contentColor = MyColors.White,
        topBar = {

            //// TOP BAR APP

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(154.dp)
                    .clip(RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp))
                    .background(MyColors.Gray)
            ) {
                Spacer(Modifier.height(30.dp))
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            scope.launch {
                                MainActivity.getAuthService().handleLogout()
                                navController?.navigate(AuthRoute)
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(MyColors.LightRed)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Sharp.ExitToApp,
                                contentDescription = "exit",
                                modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
                                tint = MyColors.White
                            )
                        }
                        Text(viewModel.employeeNameTitle ?: "Savio Tasks", style = MaterialTheme.typography.titleLarge)
                    }
                    Text(viewModel.employeeStoreName, style = MaterialTheme.typography.titleMedium)
                    ActualTime()
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Data: ", color = MyColors.LightGray, style = MaterialTheme.typography.bodyMedium)
                        Box (
                            modifier = Modifier
                                .width(112.dp)
                                .height(29.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MyColors.DarkGray)
                                .padding(start = 5.dp)
                                .clickable {
                                    viewModel.showDatePicker = true
                                }
                        ){
                            Text(text = Utils.getMillisFormatted(viewModel.selectedDateFilter, "dd/MM/yyyy"), style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterStart))
                        }
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Status: ", color = MyColors.LightGray, style = MaterialTheme.typography.bodyMedium)
                        Row (
                            modifier = Modifier
                                .width(112.dp)
                                .height(29.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(MyColors.DarkGray)
                                .padding(start = 5.dp)
                                .clickable { viewModel.showBottomSheet = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(viewModel.selectedStatusFilter.desc, style = MaterialTheme.typography.bodySmall)
                            Icon(Icons.Default.KeyboardArrowDown, "selecionar status", Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        // BOTTOM APP BAR
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .clip(RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp))
                    .background(MyColors.Gray)
            ) {
                Row (
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Box(
                        modifier = Modifier
                            .width(129.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MyColors.Lime)
                            .clickable {
                                viewModel.handleDistributeTasks()
                            }
                    ) {
                        Text("Distribuir Tarefas", style = MaterialTheme.typography.bodyLarge, fontSize = 15.sp, color = MyColors.White, modifier = Modifier.align(Alignment.Center))
                    }
                    Box(
                        modifier = Modifier
                            .width(129.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MyColors.BlueSky)
                            .clickable {
                                viewModel.showCreateNewTask = true
                            }
                    ) {
                        Text("Nova Tarefa", style = MaterialTheme.typography.bodyLarge, color = MyColors.White, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.97f)
                    .height(187.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MyColors.Gray),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text("Tarefas", style = MaterialTheme.typography.displayLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Concluidas", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Para Hoje", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Funcionarios", style = MaterialTheme.typography.bodyLarge)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(viewModel.tasksCompleted.toString(), fontSize = 70.sp, color = MyColors.BlueSky)
                    Text(viewModel.getRealTasksAmount().toString(), fontSize = 70.sp, color = MyColors.BlueSky)
                    Text(viewModel.availableEmployees.size.toString(), fontSize = 70.sp, color = MyColors.BlueSky)
                }
            }
            Row (
                modifier = Modifier
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(thickness = 3.dp, modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .padding(0.dp, 5.dp))
                Text(text = "Disponíveis", modifier = Modifier.fillMaxWidth(0.5f), textAlign = TextAlign.Center)
                HorizontalDivider(thickness = 3.dp, modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(0.dp, 5.dp))
            }
            for (employee in viewModel.availableEmployees.filter { emp -> emp.getStatus() == EmployeeStatus.AVAILABLE }) {
                EmployeeCard(
                    name = employee.getName(),
                    isMe = (employee.getId() == MainActivity.getUserManager().getId()),
                    firstTime = employee.getWorkStart(),
                    lastTime = employee.getWorkEnd(),
                    status = employee.getStatus(),
                    tasks = employee.getTasks()
                ) {
                  task, taskStatus -> viewModel.changeTask(task, taskStatus)
                }
            }
            Row (
                modifier = Modifier
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(thickness = 3.dp, modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .padding(0.dp, 5.dp))
                Text(text = "Indisponíveis", modifier = Modifier.fillMaxWidth(0.5f), textAlign = TextAlign.Center)
                HorizontalDivider(thickness = 3.dp, modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(0.dp, 5.dp))
            }
            for (employee in viewModel.availableEmployees.filter { emp -> emp.getStatus() != EmployeeStatus.AVAILABLE }) {
                EmployeeCard(
                    name = employee.getName(),
                    isMe = (employee.getId() == MainActivity.getUserManager().getId()),
                    firstTime = employee.getWorkStart(),
                    lastTime = employee.getWorkEnd(),
                    status = employee.getStatus(),
                    tasks = employee.getTasks()
                ) {
                    task, taskStatus -> viewModel.changeTask(task, taskStatus)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskDialog(
    availableEmployees: List<Employee>,
    onDimissRequest: (TaskDTO?) -> Unit
) {
    var taskNameState by remember { mutableStateOf("") }
    var taskDescState by remember { mutableStateOf("") }

    var priorityState by remember { mutableStateOf<TaskPriority>(TaskPriority.LOW) }
    var selectedEmployeeIndexState by remember { mutableIntStateOf(-1) }
    var isFixedTaskState by remember { mutableStateOf<Boolean>(false) }

    fun handleCreate()
    {
        if (taskNameState.isBlank()) return

        val employeeId: Int? = if (selectedEmployeeIndexState != -1) {
            availableEmployees[selectedEmployeeIndexState].getId()
        } else {
            null
        }

        val taskDto: TaskDTO = TaskDTO(
            name = taskNameState,
            employee_id = employeeId,
            description = taskDescState,
            priority = priorityState.id,
            is_fixed = if (isFixedTaskState) 1 else 0,
            date = Utils.getActualDateFormatted("yyyy-MM-dd")
        )

        onDimissRequest(taskDto)
    }

    fun handleCancel()
    {
        onDimissRequest(null)
    }

    fun handleSwitchPriority()
    {
        priorityState = when(priorityState) {
            TaskPriority.LOW -> TaskPriority.MODERATE
            TaskPriority.MODERATE -> TaskPriority.HIGH
            TaskPriority.HIGH -> TaskPriority.LOW
        }
    }

    fun handleSwitchEmployee()
    {
        Log.i("EMPLYEES LIVRES", "Employess: ${availableEmployees.size}")

        val index = selectedEmployeeIndexState + 1
        selectedEmployeeIndexState = if (index >= availableEmployees.size) {
            -1
        } else {
            index
        }
    }

    fun handleSwitchIsFixed()
    {
        isFixedTaskState = !isFixedTaskState
    }

    val selectedEmployeeName = if (selectedEmployeeIndexState != -1) {
        availableEmployees[selectedEmployeeIndexState].getName()
    } else {
        "Distribuição Automatica"
    }

    var isFixedText = if (isFixedTaskState) "Sim" else "Não"

    Dialog (onDismissRequest = { onDimissRequest(null) }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 10.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MyColors.Gray)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    modifier = Modifier
                        .padding(vertical = 50.dp),
                    style = MaterialTheme.typography.titleLarge,
                    text = "Adicionar Tarefa",
                    color = MyColors.White
                )
                InputField(
                    title = "Nome da Tarefa",
                    titleSize = 17.sp,
                    titleColor = MyColors.LightGray,
                    placeHolder = "Digite aqui a tarefa",
                    placeHolderSize = 20.sp,
                    maxLines = 1,
                    value = taskNameState,
                    onValueChange = { value -> taskNameState = value }
                )
                InputField(
                    title = "Descrição da tarefa (Opcional)",
                    titleColor = MyColors.LightGray,
                    titleSize = 17.sp,
                    placeHolder = "Digite aqui a descrição",
                    placeHolderSize = 20.sp,
                    maxLines = 5,
                    minLines = 5,
                    value = taskDescState,
                    onValueChange = { value -> taskDescState = value }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                ) {
                    Text("Prioridade", style = MaterialTheme.typography.titleMedium, fontSize = 17.sp, color = MyColors.LightGray)
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MyColors.DarkGray)
                            .padding(horizontal = 5.dp, vertical = 10.dp)
                            .clickable { handleSwitchPriority() },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = priorityState.desc,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            color = MyColors.White
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                ) {
                    Text("Atribuir para", style = MaterialTheme.typography.titleMedium, fontSize = 17.sp, color = MyColors.LightGray)
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MyColors.DarkGray)
                            .padding(horizontal = 5.dp, vertical = 10.dp)
                            .clickable { handleSwitchEmployee() },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedEmployeeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            color = MyColors.White
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                ) {
                    Text("Tarefa fixa", style = MaterialTheme.typography.titleMedium, fontSize = 17.sp, color = MyColors.LightGray)
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MyColors.DarkGray)
                            .padding(horizontal = 5.dp, vertical = 10.dp)
                            .clickable { handleSwitchIsFixed() },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = isFixedText,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            color = MyColors.White
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RoundedButton(
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                        title = "Cancelar",
                        color = MyColors.LightRed
                    ) {
                        handleCancel()
                    }
                    RoundedButton (
                        title = "Criar",
                        color = MyColors.Lime
                    ) {
                        handleCreate()
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppPreview()
{
    SavioTasksTheme (
        darkTheme = true,
        dynamicColor = false
    ){
        AppScreen(null)
    }
}