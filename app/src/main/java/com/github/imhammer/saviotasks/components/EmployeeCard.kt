package com.github.imhammer.saviotasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.imhammer.saviotasks.constants.EmployeeStatus
import com.github.imhammer.saviotasks.constants.TaskPriority
import com.github.imhammer.saviotasks.constants.TaskStatus
import com.github.imhammer.saviotasks.objects.Task
import com.github.imhammer.saviotasks.ui.theme.MyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeCard(
    name: String,
    isMe: Boolean,
    firstTime: String,
    lastTime: String,
    status: EmployeeStatus,
    tasks: List<Task>,
    taskStatusChanged: ((Task, TaskStatus) -> Unit)? = null
) {
    val borderColorStatus: Color = if (status == EmployeeStatus.AVAILABLE) MyColors.BlueSky else Color.Transparent

    val sheetState = rememberModalBottomSheetState()
    var editingTaskStatus: Task? by remember { mutableStateOf(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    var percent = 0
    if (tasks.isNotEmpty()) {
        val completedTasksCount: Float = tasks.filter { task -> task.getStatus() == TaskStatus.COMPLETED }.size.toFloat()
        val percentCompleted: Float = completedTasksCount / tasks.size.toFloat()
        percent = (percentCompleted * 100).toInt()
    }

    LaunchedEffect(showBottomSheet)
    {
        if (!showBottomSheet) {
            editingTaskStatus = null
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = MyColors.Gray,
            contentColor = MyColors.White
        ) {
            OptionsList(
                options = listOf(
                    OptionItem("Pendente", MyColors.Yellow, TaskStatus.PENDING),
                    OptionItem("Em Andamento", MyColors.DarkGray, TaskStatus.INPROGRESS),
                    OptionItem("Concluido", MyColors.Lime, TaskStatus.COMPLETED)
                )
            ) {
//                editingTaskStatus?.setStatus(it.data)
                showBottomSheet = false

                if (taskStatusChanged != null) {
                    taskStatusChanged(editingTaskStatus!!, it.data)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.97f)
            .wrapContentHeight()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, borderColorStatus, shape = RoundedCornerShape(10.dp))
            .background(Color.Transparent)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(141.dp)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(MyColors.DarkGray)
                .padding(10.dp)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name, style = MaterialTheme.typography.titleLarge, fontSize = 25.sp)
                    if (isMe) {
                        Spacer(Modifier.padding(horizontal = 5.dp))
                        Box (
                            modifier = Modifier
                                .width(87.dp)
                                .height(25.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MyColors.BlueSky)
                        ) {
                            Text("Você", style = MaterialTheme.typography.titleSmall, modifier = Modifier.align(
                                Alignment.Center))
                        }
                    }
                }
                Column (
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                ) {
                    Text("\uD83D\uDD52 Horário: $firstTime - $lastTime", style = MaterialTheme.typography.bodySmall, fontSize = 16.sp)
                    Text("${tasks?.size} tarefa${(if (tasks?.size!! > 1) "s" else "")} • ${percent}% concluído", style = MaterialTheme.typography.bodySmall, fontSize = 16.sp, modifier = Modifier.padding(start = 6.dp))
                }
            }
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(end = 22.dp, top = 30.dp)
            ) {
                val statusColor: Color = when (status) {
                    EmployeeStatus.AVAILABLE -> MyColors.Lime
                    EmployeeStatus.DAYOFF -> MyColors.Yellow
                    EmployeeStatus.INOFF -> MyColors.LightRed
                }
                Box (
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(5.dp))
                        .background(statusColor)
                ){
                    Text(status.desc, style = MaterialTheme.typography.bodySmall, fontSize = 15.sp, modifier = Modifier
                        .align(Alignment.Center)
                        .padding(5.dp))
                }
            }
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(MyColors.Gray)
                .padding(0.dp, 30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (task in tasks) {
                key (task) {
                    TaskCard(task, isMe) {
                        editingTaskStatus = task
                        showBottomSheet = true
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    enabled: Boolean = true,
    onStatusClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .width(339.dp)
            .heightIn(min = 65.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MyColors.DarkGray)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row (
            modifier = Modifier
                .widthIn(max = 180.dp)
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box (
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
                    .clip(CircleShape)
                    .background(taskPriorityColor(task.getPriority()))
            ) {
                if (task.isFixed()) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "",
                        modifier = Modifier
                            .width(15.dp)
                            .height(15.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            Text(
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 15.sp,
                text = task.getText()
            )
        }
        Row (
            modifier = Modifier
                .width(121.dp)
                .height(23.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(taskStatusColor(task.getStatus()))
                .padding(horizontal = 5.dp)
                .alpha(if (enabled) 1f else 0.6f)
                .clickable {
                    if (enabled) onStatusClick()
                },
            horizontalArrangement = if (enabled) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(task.getStatus().desc, color = Color.Black, style = MaterialTheme.typography.bodySmall)
            if (enabled) {
                Icon(Icons.Default.KeyboardArrowDown, "mudar status da tarefa", tint = Color.Black)
            }
        }
    }
}

fun taskStatusColor(taskStatus: TaskStatus): Color
{
    return when(taskStatus) {
        TaskStatus.ALL -> MyColors.LightGray
        TaskStatus.INPROGRESS -> MyColors.Gray
        TaskStatus.COMPLETED -> MyColors.Lime
        TaskStatus.PENDING -> MyColors.Yellow
    }
}

fun taskPriorityColor(taskPriority: TaskPriority): Color
{
    return when(taskPriority) {
        TaskPriority.LOW -> MyColors.Yellow
        TaskPriority.MODERATE -> MyColors.BlueSky
        TaskPriority.HIGH -> MyColors.LightRed
    }
}