package com.github.imhammer.saviotasks.services

import com.github.imhammer.saviotasks.dto.AuthDTO
import com.github.imhammer.saviotasks.dto.AuthResponseDTO
import com.github.imhammer.saviotasks.dto.ChangeStatusDTO
import com.github.imhammer.saviotasks.dto.CreateTaskDTO
import com.github.imhammer.saviotasks.dto.DistributeDTO
import com.github.imhammer.saviotasks.dto.EmployeeDTO
import com.github.imhammer.saviotasks.dto.FcmTokenDTO
import com.github.imhammer.saviotasks.dto.GenericResponseMessage
import com.github.imhammer.saviotasks.dto.StoreDTO
import com.github.imhammer.saviotasks.dto.TaskDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SavioApiRepository
{
    @Headers("Content-Type: application/json")
    @POST("/api/login")
    suspend fun authenticate(@Body body: AuthDTO): Response<AuthResponseDTO>

    @Headers("Content-Type: aplication/json")
    @GET("/api/employees")
    suspend fun readEmployees(@Header("Authorization") authToken: String): Response<List<EmployeeDTO>>

    @Headers("Content-Type: application/json")
    @GET("/api/employees/available")
    suspend fun readAvailableEmployees(
        @Header("Authorization") authToken: String,
        @Query("date") date: String,
        @Query("storeId") storeId: String
    ): Response<List<EmployeeDTO>>

    @Headers("Content-Type: application/json")
    @GET("/api/tasks")
    suspend fun readTasks(
        @Header("Authorization") authToken: String,
        @Query("storeId") storeId: String,
        @Query("date") date: String,
        @Query("status") status: String? = null
    ): Response<List<TaskDTO>>

    @Headers("Content-Type: application/json")
    @PATCH("/api/tasks/{id}/status")
    suspend fun changeTaskStatus(
        @Header("Authorization") authToken: String,
        @Path("id") id: Int,
        @Body body: ChangeStatusDTO
    ): Response<GenericResponseMessage>

    @Headers("Content-Type: application/json")
    @POST("/api/tasks")
    suspend fun createTask(
        @Header("Authorization") authToken: String,
        @Body body: CreateTaskDTO
    ): Response<GenericResponseMessage>

    @Headers("Content-Type: application/json")
    @POST("/api/tasks/distribute")
    suspend fun distributeTasks(
        @Header("Authorization") authToken: String,
        @Body body: DistributeDTO
    ): Response<GenericResponseMessage>

    @Headers("Content-Type: application/json")
    @POST("/api/fcmtoken")
    suspend fun sendFCMToken(
        @Header("Authorization") authToken: String,
        @Body body: FcmTokenDTO
    ): Response<GenericResponseMessage>
}