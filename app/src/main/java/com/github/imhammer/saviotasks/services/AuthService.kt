package com.github.imhammer.saviotasks.services

import android.content.Context
import android.util.Log
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.github.imhammer.saviotasks.MainActivity
import com.github.imhammer.saviotasks.dto.AuthDTO
import com.github.imhammer.saviotasks.dto.AuthResponseDTO
import com.github.imhammer.saviotasks.dto.EmployeeDTO
import com.github.imhammer.saviotasks.dto.GenericResponseMessage
import okhttp3.ResponseBody
import retrofit2.Retrofit

class AuthService(context: Context, retrofit: Retrofit, private val repository: SavioApiRepository)
{
    private val converter = retrofit.responseBodyConverter<GenericResponseMessage>(GenericResponseMessage::class.java, arrayOfNulls(0))
    private var lastRequestError: GenericResponseMessage? = null


    fun getLastRequestError(): GenericResponseMessage?
    {
        return lastRequestError
    }

    suspend fun parseErrorBody(errorBody: ResponseBody): GenericResponseMessage?
    {
        return try {
            converter.convert(errorBody)
        } catch (e: Exception) {
            null
        }
    }

    fun handleLogout()
    {
        MainActivity.getUserManager().clearCredentials()
        MainActivity.getUserManager().saveStoreId(-1)
        MainActivity.getUserManager().saveStoreName("NOT-FOUND")
    }

    suspend fun handleLoginSuccess(authResponseDTO: AuthResponseDTO): Boolean
    {
        if (!MainActivity.getUserManager().saveCredentialsAndPreInfos(authResponseDTO)) {
            return false
        }

        Log.i("AUTHSERVICE", "Passou do saveCredentials")
        return handlePreApp()
    }

    suspend fun handlePreApp(): Boolean
    {
        if (!MainActivity.getUserManager().hasCredentials()) {
            return false
        }

        if (!MainActivity.getUserManager().isValid()) {
            return false
        }

        val employee = MainActivity.getApiService().findUserById()
        if (employee == null) {
            MainActivity.getUserManager().clearCredentials()
            return false
        }

        MainActivity.getUserManager().saveStoreId(employee.store_id)
        MainActivity.getUserManager().saveStoreName(employee.store_name)

        val fcmToken = MainActivity.getUserManager().getFcmToken()
        if (fcmToken != null) {
            MainActivity.getApiService().sendFCMToken(fcmToken)
        }
        return true
    }

    suspend fun authenticate(username: String, password: String): Boolean
    {
        val response = try {
            repository.authenticate(AuthDTO(username, password))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        val responseBody = response.body()

        if (!response.isSuccessful || responseBody == null) {
            response.errorBody()?.let { errorBody ->
                parseErrorBody(errorBody)?.let {
                    it.code = response.code()
                    lastRequestError = it
                }
            }
            return false
        }

        return handleLoginSuccess(responseBody)
//        var authResponseDTO: AuthResponseDTO? = null
//        val response = try {
//            repository.authenticate(AuthDTO(username, password))
//        } catch (e: Exception) {
//            null
//        }
//
//        if (response != null) {
//            if (response.isSuccessful) {
//                val body = response.body()
//                if (body != null) {
//                    val jwt = try {
//                        JWT(body.token ?: "")
//                    } catch (e: DecodeException) {
//                        Log.e("AUTH ERROR", "Error on decodeJwt: ", e)
//                        null
//                    }
//
//                    if (jwt != null && handleLoginSuccess(body, jwt)) {
//                        authResponseDTO = body
//                    }
//                }
//            } else {
//                val errorBody = response.errorBody()
//                if (errorBody != null) {
//                    authResponseDTO = parseErrorBody(errorBody)
//                }
//            }
//        }
//
//        return authResponseDTO
    }
}