package com.example.aplikasibast

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("bast_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
    }

    init {
        // Set default jika kosong (untuk simulasi magang)
        if (getUserName().isNullOrEmpty()) {
            saveUser("Trisnualdi", "Teknisi")
        }
    }

    fun saveUser(name: String, role: String) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, "Trisnualdi")
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, "Teknisi")
}
