package com.github.imhammer.saviotasks.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.imhammer.saviotasks.MainActivity.Companion.getUserManager

class StartViewModel() : ViewModel()
{
    var loggedIn by mutableStateOf(getUserManager().isValid())

    fun setLogged(logged: Boolean = true)
    {
        loggedIn = logged
    }
}