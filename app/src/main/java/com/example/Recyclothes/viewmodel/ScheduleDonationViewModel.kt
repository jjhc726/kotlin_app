package com.example.Recyclothes.viewmodel


import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.remote.ScheduledDonationDto
import com.example.Recyclothes.data.repository.OfflineScheduledDonationRepository
import com.example.Recyclothes.data.repository.ScheduleDonationRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class ScheduleDonationViewModel(app: Application) : AndroidViewModel(app) {
    val title = mutableStateOf("")
    val date = mutableStateOf("")
    val time = mutableStateOf("")
    val note = mutableStateOf("")
    val clothingType = mutableStateOf("")
    val size = mutableStateOf("")
    val brand = mutableStateOf("")

    val titleError = mutableStateOf<String?>(null)
    val dateError  = mutableStateOf<String?>(null)
    val timeError  = mutableStateOf<String?>(null)
    val typeError  = mutableStateOf<String?>(null)
    val sizeError  = mutableStateOf<String?>(null)
    val brandError = mutableStateOf<String?>(null)

    private val onlineRepo = ScheduleDonationRepository()
    private val offlineRepo = OfflineScheduledDonationRepository(app)

    private fun sessionEmail(): String? =
        FirebaseAuth.getInstance().currentUser?.email
            ?: getApplication<Application>()
                .getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("email", null)
                ?.trim()
                ?.lowercase()

    private fun parseDateTimeMillis(date: String, time: String): Long? = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        sdf.parse("$date $time")!!.time
    } catch (_: Exception) { null }

    private fun validate(): Boolean {
        val missing = mutableListOf<String>()
        if (title.value.isBlank()) { titleError.value = "Required"; missing += "Title" } else titleError.value = null
        if (date.value.isBlank())  { dateError.value  = "Required"; missing += "Date" }  else dateError.value = null
        if (time.value.isBlank())  { timeError.value  = "Required"; missing += "Time" }  else timeError.value = null
        if (clothingType.value.isBlank()) { typeError.value = "Required"; missing += "Clothing Type" } else typeError.value = null
        if (size.value.isBlank())  { sizeError.value  = "Required"; missing += "Size" }  else sizeError.value = null
        if (brand.value.isBlank()) { brandError.value = "Required"; missing += "Brand" } else brandError.value = null

        if (missing.isNotEmpty()) {
            return false
        }
        return true
    }


    fun submit(
        onOnlineSuccess: () -> Unit,
        onQueuedOffline: (String) -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        if (!validate()) {
            onError("Please fix the highlighted fields.")
            return@launch
        }
        val email = sessionEmail()
        if (email.isNullOrBlank()) {
            onError("No active session; please log in.")
            return@launch
        }
        val millis = parseDateTimeMillis(date.value, time.value)
        if (millis == null) {
            onError("Date/Time must be yyyy-MM-dd and HH:mm.")
            return@launch
        }


        val ok = onlineRepo.create(
            ScheduledDonationDto(
                title = title.value,
                dateMillis = millis,
                timeText = time.value,
                note = note.value.ifBlank { null },
                clothingType = clothingType.value,
                size = size.value,
                brand = brand.value,
                userEmail = email
            )
        )
        if (ok) {
            onOnlineSuccess()
        } else {
            offlineRepo.queue(
                userEmail = email,
                title = title.value,
                dateMillis = millis,
                timeText = time.value,
                note = note.value.ifBlank { null },
                clothingType = clothingType.value,
                size = size.value,
                brand = brand.value
            )
            onQueuedOffline("No connectivity. Saved offline and will sync later.")
        }
    }
}
