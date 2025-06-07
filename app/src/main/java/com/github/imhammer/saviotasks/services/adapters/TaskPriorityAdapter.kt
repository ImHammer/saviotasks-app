package com.github.imhammer.saviotasks.services.adapters

import com.github.imhammer.saviotasks.constants.TaskPriority
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class TaskPriorityAdapter : TypeAdapter<TaskPriority>()
{
    override fun write(out: JsonWriter, value: TaskPriority?)
    {
        out.value(value?.id)
    }

    override fun read(`in`: JsonReader): TaskPriority
    {
        val priorityValue = `in`.nextInt()
        return TaskPriority.of(priorityValue) ?: TaskPriority.LOW
    }
}