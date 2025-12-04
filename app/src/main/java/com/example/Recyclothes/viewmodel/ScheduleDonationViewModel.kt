package com.example.Recyclothes.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.remote.ScheduledDonationDto
import com.example.Recyclothes.data.remote.ScheduleDonationDraft
import com.example.Recyclothes.data.repository.EngagementRepository
import com.example.Recyclothes.data.repository.OfflineScheduledDonationRepository
import com.example.Recyclothes.data.repository.ScheduleDonationDraftRepository
import com.example.Recyclothes.data.repository.ScheduleDonationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(FlowPreview::class)
class ScheduleDonationViewModel(app: Application) : AndroidViewModel(app) {

    val title         = mutableStateOf("")
    val date          = mutableStateOf("")
    val time          = mutableStateOf("")
    val note          = mutableStateOf("")
    val clothingType  = mutableStateOf("")
    val size          = mutableStateOf("")
    val brand         = mutableStateOf("")

    private val net = ConnectivityObserver(app)

    val titleError = mutableStateOf<String?>(null)
    val dateError  = mutableStateOf<String?>(null)
    val timeError  = mutableStateOf<String?>(null)
    val typeError  = mutableStateOf<String?>(null)
    val sizeError  = mutableStateOf<String?>(null)
    val brandError = mutableStateOf<String?>(null)

    private val onlineRepo  = ScheduleDonationRepository()
    private val offlineRepo = OfflineScheduledDonationRepository(app)
    private val draftRepo   = ScheduleDonationDraftRepository(app)
    private val engagementRepo = EngagementRepository()

    private val autoSave = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        net.onlineFlow().onEach { isOnline ->
            if (isOnline) {
                viewModelScope.launch { offlineRepo.flushPending(onlineRepo) }
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            draftRepo.load(draftKey())?.let { applyDraft(it) }
        }

        autoSave
            .debounce(400)
            .onEach { draftRepo.save(draftKey(), currentDraft()) }
            .launchIn(viewModelScope)
    }

    fun onFieldEdited() { autoSave.tryEmit(Unit) }

    private fun sessionEmail(): String? =
        FirebaseAuth.getInstance().currentUser?.email
            ?: getApplication<Application>()
                .getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("email", null)
                ?.trim()
                ?.lowercase()

    private fun draftKey(): String = (sessionEmail() ?: "guest").lowercase()

    private fun currentDraft() = ScheduleDonationDraft(
        title = title.value,
        date  = date.value,
        time  = time.value,
        note  = note.value,
        clothingType = clothingType.value,
        size  = size.value,
        brand = brand.value
    )

    private fun applyDraft(d: ScheduleDonationDraft) {
        title.value = d.title
        date.value  = d.date
        time.value  = d.time
        note.value  = d.note
        clothingType.value = d.clothingType
        size.value  = d.size
        brand.value = d.brand
    }

    private fun parseDateTimeMillis(date: String, time: String): Long? = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        sdf.parse("$date $time")!!.time
    } catch (_: Exception) { null }

    private fun validate(): Boolean {
        var ok = true
        if (title.value.isBlank())       { titleError.value = "Required"; ok = false } else titleError.value = null
        if (date.value.isBlank())        { dateError.value  = "Required"; ok = false }  else dateError.value = null
        if (time.value.isBlank())        { timeError.value  = "Required"; ok = false }  else timeError.value = null
        if (clothingType.value.isBlank()){ typeError.value  = "Required"; ok = false }  else typeError.value = null
        if (size.value.isBlank())        { sizeError.value  = "Required"; ok = false }  else sizeError.value = null
        if (brand.value.isBlank())       { brandError.value = "Required"; ok = false }  else brandError.value = null
        return ok
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
            draftRepo.clear(draftKey())
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

    fun onScheduleDonationSelected() {
        viewModelScope.launch { engagementRepo.logScheduleDonation() }
    }
}