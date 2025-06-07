package com.github.imhammer.saviotasks.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.imhammer.saviotasks.MainActivity
import kotlinx.coroutines.launch

class AuthScreenViewModel() : ViewModel()
{
    var responseStatusMessage by mutableStateOf("")
    var authSuccess by mutableStateOf(false)

    fun handleLogin(username: String, password: String)
    {
        if (
            username.isBlank() or
            username.isEmpty() or
            password.isBlank() or
            password.isEmpty()
        ) {
            responseStatusMessage = "Os campos estão vazios"
            return
        }

        viewModelScope.launch {
            if (MainActivity.getAuthService().authenticate(username, password)) {
                responseStatusMessage = "Login efetuado com sucesso!"
                authSuccess = true
                MainActivity.getUserManager().setLastUsername(username)
            } else {
                MainActivity.getAuthService().getLastRequestError()?.let {
                    responseStatusMessage = it.message ?: "Erro ao efetuar o login"
                } ?: run {
                    responseStatusMessage = "Não foi possível conectar ao Servidor"
                }
            }
        }
    }
}