package com.example.Recyclothes.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import androidx.core.content.edit

class PreferenceHelper(context: Context) {

    private val prefs = context.getSharedPreferences("donation_prefs", Context.MODE_PRIVATE)

    fun save(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun load(key: String): String {
        return prefs.getString(key, "") ?: ""
    }

    fun saveList(key: String, list: List<String>) {
        prefs.edit { putString(key, list.joinToString(",")) }
    }

    fun loadList(key: String): List<String> {
        val raw = prefs.getString(key, "") ?: ""
        return if (raw.isBlank()) emptyList() else raw.split(",")
    }

    fun saveBitmap(key: String, bitmap: Bitmap?) {
        if (bitmap == null) {
            prefs.edit { remove(key) }
            return
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val encoded = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
        prefs.edit { putString(key, encoded) }
    }

    fun loadBitmap(key: String): Bitmap? {
        val encoded = prefs.getString(key, null) ?: return null
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}
