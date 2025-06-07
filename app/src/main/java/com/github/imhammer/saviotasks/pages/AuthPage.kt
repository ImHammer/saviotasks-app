package com.github.imhammer.saviotasks.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.imhammer.saviotasks.MainActivity
import com.github.imhammer.saviotasks.components.InputField
import com.github.imhammer.saviotasks.components.MinimalPopupNotification
import com.github.imhammer.saviotasks.components.RoundedButton
import com.github.imhammer.saviotasks.views.AuthScreenViewModel
import com.github.imhammer.saviotasks.ui.theme.MyColors
import com.github.imhammer.saviotasks.ui.theme.SavioTasksTheme

@Composable
fun AuthScreen(navController: NavController?, executeLoggedIn: ((Boolean) -> Unit)?)
{
    val viewModel = remember { AuthScreenViewModel() }

    var userInput by remember { mutableStateOf(MainActivity.getUserManager().getLastUsername() ?: "") }
    var passInput by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.authSuccess)
    {
        if (executeLoggedIn != null) {
            executeLoggedIn(viewModel.authSuccess)
        }
    }

    if (viewModel.responseStatusMessage.isNotBlank()) {
        MinimalPopupNotification(viewModel.responseStatusMessage) {
            viewModel.responseStatusMessage = "";
        }
    }

    Scaffold (
        containerColor = MyColors.Gray,
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Savio Tasks", style = MaterialTheme.typography.displayLarge)
                Text("Faça login para continuar", style = MaterialTheme.typography.titleMedium, color = MyColors.LightGray)
            }
            InputField(
                title = "Usúario",
                placeHolder = "Digite seu Usuario",
                maxLines = 1,
                onValueChange = {
                    changed -> userInput = changed
                },
                value = userInput
            )
            InputField(
                title = "Senha",
                placeHolder = "Digite sua Senha",
                maxLines = 1,
                onValueChange = { changed -> passInput = changed },
                value = passInput
            )
            RoundedButton(title = "Entrar", color = MyColors.BlueSky, modifier = Modifier.fillMaxWidth(0.5f)) { viewModel.handleLogin(userInput, passInput) }
            Text("Savio | Dioney | Danilo", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 100.dp))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AuthScreenPreview()
{
    SavioTasksTheme (
        darkTheme = false,
        dynamicColor = false
    ){
        AuthScreen(null, null)
    }
}
