package com.github.imhammer.saviotasks

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.imhammer.saviotasks.constants.NotifyStatus
import com.github.imhammer.saviotasks.objects.Notification
import com.github.imhammer.saviotasks.pages.AppScreen
import com.github.imhammer.saviotasks.pages.AuthScreen
import com.github.imhammer.saviotasks.services.ApiService
import com.github.imhammer.saviotasks.services.AuthService
import com.github.imhammer.saviotasks.services.SavioApiRepository
import com.github.imhammer.saviotasks.services.UserManager
import com.github.imhammer.saviotasks.ui.theme.MyColors
import com.github.imhammer.saviotasks.ui.theme.SavioTasksTheme
import com.github.imhammer.saviotasks.views.StartViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.time.Duration
import android.content.pm.PackageManager
import com.github.imhammer.saviotasks.constants.TaskPriority
import com.github.imhammer.saviotasks.services.adapters.TaskPriorityAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder


class MainActivity : ComponentActivity()
{
    private lateinit var CHANNEL_ID: String

    companion object {
        private lateinit var authService: AuthService
        private lateinit var apiService: ApiService
        private lateinit var userManager: UserManager
        private lateinit var retrofit: Retrofit

        fun getApiService(): ApiService = apiService
        fun getAuthService(): AuthService = authService
        fun getUserManager(): UserManager = userManager
        fun getRetrofit(): Retrofit = retrofit
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        checkAndRequestNotificationPermission()

        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(TaskPriority::class.java, TaskPriorityAdapter())
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.server_url))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiRepository = retrofit.create(SavioApiRepository::class.java)

        apiService  = ApiService(applicationContext, retrofit, apiRepository)
        authService = AuthService(applicationContext, retrofit, apiRepository)
        userManager = UserManager(applicationContext)

        Log.i("MAINACTIVITY", "Antes do Handle")

//        userManager.clearCredentials()

        runBlocking {
            if (!getAuthService().handlePreApp()) {
                getAuthService().handleLogout()
            }
        }

        Log.i("MAINACTIVITY", "Depois do handle")

        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MAINACTIVITY", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result

            GlobalScope.launch {
                getApiService().sendFCMToken(token)
            }
        }

        enableEdgeToEdge()
        setContent {
            var notificationsList = remember { mutableStateListOf<Notification>() }
            val startViewModel = remember { StartViewModel() }
            val navController = rememberNavController()
            val lastError by getApiService().externaleState.collectAsState()

            val addNotification = fun (message: String, notifyStatus: NotifyStatus)
            {
                notificationsList.add(Notification(message, notifyStatus.color))
            }

            LaunchedEffect(lastError)
            {
                if (lastError?.code == 401) {
                    addNotification("Faça o login e tente novamente", NotifyStatus.DANGER)
                    startViewModel.loggedIn = false
                }
            }

            LaunchedEffect(notificationsList.size)
            {
                delay(Duration.parse("2s"))
                notificationsList.removeFirstOrNull()
            }

            //// MODAL PARA NOTIFICAÇÕES
            if (notificationsList.size > 0) {
                Popup(
                    onDismissRequest = { notificationsList.clear() },
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
                            items(notificationsList.size) { notfyIndex ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(notificationsList[notfyIndex].color)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = notificationsList[notfyIndex].text,
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

            SavioTasksTheme (
                darkTheme = false,
                dynamicColor = false
            ){
                NavHost(navController = navController, startDestination = if (startViewModel.loggedIn) AppRoute else AuthRoute) {
                    composable<AuthRoute> { AuthScreen(navController) {
                        startViewModel.loggedIn = it
                    }
                    }
                    composable<AppRoute>  { AppScreen(navController) }
                }
            }
        }
    }

    fun requestPermissionLauncher(permission: String)
    {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Ok, agora você pode receber as notificações!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Você não poderá receber as notificações!", Toast.LENGTH_SHORT).show()
            }
        }.launch(permission)
    }

    private fun checkAndRequestNotificationPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher(android.Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}