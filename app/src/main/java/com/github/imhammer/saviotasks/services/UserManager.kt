package com.github.imhammer.saviotasks.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.auth0.android.jwt.JWT
import com.github.imhammer.saviotasks.dto.AuthResponseDTO

class UserManager(context: Context)
{
    private var sharedPreferences: SharedPreferences
    private var cacheStoreId: Int = 0
    private var cacheStoreName: String = "NOT_FOUND"

    init {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "user_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_ADMIN = "isAdmin"
        private const val KEY_NAME  = "employeeName"
        private const val KEY_EMPLOYEE_ID  = "employeeId"
        private const val KEY_USERNAME  = "username"
        private const val KEY_LAST_USERNAME = "last_username"

        private const val KEY_FCM_TOKEN = "fcm_token"
    }

    fun hasCredentials(): Boolean
    {
        if (!sharedPreferences.contains(KEY_TOKEN)) return false
        if (!sharedPreferences.contains(KEY_EMPLOYEE_ID)) return false

        return true
    }

    fun saveCredentialsAndPreInfos(authResponseDTO: AuthResponseDTO): Boolean
    {
        val jwt = try {
            JWT(authResponseDTO.token ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        val id: Int =
            if (jwt.claims.containsKey("employeeId"))
                jwt.getClaim("employeeId").asInt() ?: -1
            else
                jwt.getClaim("id").asInt() ?: -1

        val username: String = jwt.getClaim("username").asString() ?: ""
        val employeeName: String = jwt.getClaim("employeeName").asString() ?: ""
        val isAdmin: Int = jwt.getClaim("isAdmin").asInt() ?: 0

        sharedPreferences.edit()
            .putString(KEY_TOKEN, authResponseDTO.token)
            .putInt(KEY_ADMIN, isAdmin)
            .putString(KEY_NAME, employeeName)
            .putInt(KEY_EMPLOYEE_ID, id)
            .putString(KEY_USERNAME, username)
            .apply()

        return true
    }

    fun saveStoreId(storeId: Int)
    {
        cacheStoreId = storeId
    }

    fun saveStoreName(storeName: String)
    {
        cacheStoreName = storeName
    }

    fun saveFcmToken(token: String)
    {
        sharedPreferences.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun clearCredentials() {
        sharedPreferences.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_ADMIN)
            .remove(KEY_NAME)
            .remove(KEY_EMPLOYEE_ID)
            .remove(KEY_USERNAME)
            .apply()
    }

    fun clearAll() { sharedPreferences.edit().clear().apply() }

    fun setLastUsername(username: String)
    {
        sharedPreferences.edit().putString(KEY_LAST_USERNAME, username).apply()
    }

    fun getLastUsername(): String?
    {
        return sharedPreferences.getString(KEY_LAST_USERNAME, null)
    }

    fun getToken(): String?    = sharedPreferences.getString(KEY_TOKEN, null);
    fun getAdmin(): Boolean    = sharedPreferences.getInt(KEY_ADMIN, 0) == 1;
    fun getName(): String?     = sharedPreferences.getString(KEY_NAME, null);
    fun getId(): Int           = sharedPreferences.getInt(KEY_EMPLOYEE_ID, -1);
    fun getUsername(): String? = sharedPreferences.getString(KEY_USERNAME, null);
    fun getFcmToken(): String? = sharedPreferences.getString(KEY_FCM_TOKEN, null);

    fun getStoreId(): Int      = cacheStoreId
    fun getStoreName(): String = cacheStoreName

    fun getTokenToRequest(): String = "Bearer ${getToken()}"

    fun getEmployeeId() = getId()

    fun isValid(): Boolean
    {
        try {
            val jwt: JWT = JWT(getToken() ?: "")
            if (jwt.isExpired(System.currentTimeMillis())) {
                clearCredentials()
                return false
            }
            if (!jwt.claims.containsKey("employeeId") && !jwt.claims.containsKey("id")) {
                clearCredentials()
                return false
            }

            val employeeId: Int? = if (jwt.claims.containsKey("employeeId")) jwt.getClaim("employeeId").asInt()
                else jwt.getClaim("id").asInt()

            if (employeeId == null) {
                clearCredentials()
                return false
            }

            if (getEmployeeId() != employeeId) {
                clearCredentials()
                return false
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }
}