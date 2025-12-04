package com.example.Recyclothes.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri

class PreferenceHelper(context: Context) {

    private val prefs = context.getSharedPreferences("donation_prefs", Context.MODE_PRIVATE)

    // ---------------------------------------------------------
    // STRINGS
    // ---------------------------------------------------------
    fun save(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun load(key: String): String {
        return prefs.getString(key, "") ?: ""
    }

    // ---------------------------------------------------------
    // LISTAS
    // ---------------------------------------------------------
    fun saveList(key: String, list: List<String>) {
        prefs.edit { putString(key, list.joinToString(",")) }
    }

    fun loadList(key: String): List<String> {
        val raw = prefs.getString(key, "") ?: ""
        return if (raw.isBlank()) emptyList() else raw.split(",")
    }

    fun saveUri(key: String, uri: Uri?) {
        if (uri == null) {
            prefs.edit { remove(key) }
        } else {
            prefs.edit { putString(key, uri.toString()) }
        }
    }

    fun loadUri(key: String): Uri? {
        val raw = prefs.getString(key, null) ?: return null
        return raw.toUri()
    }
    fun clearAll() {
        prefs.edit { clear() }
    }
}
