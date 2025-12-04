package com.example.Recyclothes.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.remote.ScheduleDonationDraft
import com.example.Recyclothes.data.remote.ScheduledDonationDto
import com.example.Recyclothes.data.repository.EngagementRepository
import com.example.Recyclothes.data.repository.OfflineScheduledDonationRepository
import com.example.Recyclothes.data.repository.ScheduleDonationRepository
import com.example.Recyclothes.data.repository.ScheduleDonationDraftRepository
import com.example.Recyclothes.data.repository.ScheduleDonationDraftsRdbRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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

    private val onlineRepo  = ScheduleDonationRepository()
    private val offlineRepo = OfflineScheduledDonationRepository(app)
    private val engagementRepo = EngagementRepository()
    private val net = ConnectivityObserver(app)

    private val draftRepo = ScheduleDonationDraftRepository(app)

    private val schedDraftsRepo = ScheduleDonationDraftsRdbRepository(app)

    private var currentDraftId: String? = null

    private val autoSave = MutableStateFlow(Unit)

    init {
        viewModelScope.launch {
            draftRepo.load(draftKey())?.let { d ->
                title.value = d.title
                date.value  = d.date
                time.value  = d.time
                note.value  = d.note
                clothingType.value = d.clothingType
                size.value  = d.size
                brand.value = d.brand
            }
        }

        net.onlineFlow()
            .onEach { isOnline -> if (isOnline) viewModelScope.launch { offlineRepo.flushPending(onlineRepo) } }
            .launchIn(viewModelScope)

        setupAutosave()
    }

    fun persistDraftNow() {
        viewModelScope.launch {
            draftRepo.save(
                draftKey(),
                ScheduleDonationDraft(
                    title = title.value,
                    date = date.value,
                    time = time.value,
                    note = note.value,
                    clothingType = clothingType.value,
                    size = size.value,
                    brand = brand.value
                )
            )
        }
    }
    private fun sessionEmail(): String? =
        FirebaseAuth.getInstance().currentUser?.email
            ?: getApplication<Application>()
                .getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("email", null)
                ?.trim()?.lowercase()

    private fun draftKey(): String {
        val email = sessionEmail() ?: "anonymous"
        val did = currentDraftId
        return if (did != null) "$email#schedDraft:$did" else email
    }

    fun loadDraftById(draftId: String) {
        viewModelScope.launch {
            val d = withContext(Dispatchers.IO) { schedDraftsRepo.getById(draftId) } ?: return@launch
            currentDraftId = d.draftId
            title.value = d.title
            date.value  = d.date
            time.value  = d.time
            note.value  = d.note ?: ""
            clothingType.value = d.clothingType
            size.value  = d.size
            brand.value = d.brand
            autoSave.tryEmit(Unit)
        }
    }

    fun saveDraftNow(onSaved: (String) -> Unit) {
        viewModelScope.launch {
            val email = sessionEmail() ?: "anonymous@local"
            val id = withContext(Dispatchers.IO) {
                schedDraftsRepo.createOrUpdate(
                    userEmail = email,
                    title = title.value,
                    date = date.value,
                    time = time.value,
                    note = note.value.ifBlank { null },
                    clothingType = clothingType.value,
                    size = size.value,
                    brand = brand.value,
                    existingId = currentDraftId
                )
            }
            currentDraftId = id
            onSaved("Draft saved locally.")
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupAutosave() {
        autoSave
            .debounce(400)
            .onEach {
                draftRepo.save(
                    draftKey(),
                    ScheduleDonationDraft(
                        title = title.value,
                        date  = date.value,
                        time  = time.value,
                        note  = note.value,
                        clothingType = clothingType.value,
                        size  = size.value,
                        brand = brand.value
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    fun onFieldEdited() { autoSave.tryEmit(Unit) }

    private fun parseDateTimeMillis(date: String, time: String): Long? = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        sdf.parse("$date $time")!!.time
    } catch (_: Exception) { null }

    private fun validate(): Boolean {
        var ok = true
        fun req(s: String, setErr: (String?) -> Unit) =
            if (s.isBlank()) { setErr("Required"); ok = false } else setErr(null)
        req(title.value){ titleError.value = it }
        req(date.value ){ dateError.value  = it }
        req(time.value ){ timeError.value  = it }
        req(clothingType.value){ typeError.value = it }
        req(size.value ){ sizeError.value  = it }
        req(brand.value){ brandError.value = it }
        return ok
    }

    private suspend fun clearDraftAndResetForm() {
        val keyToClear = draftKey()
        currentDraftId?.let { withContext(Dispatchers.IO) { schedDraftsRepo.deleteById(it) } }
        currentDraftId = null

        draftRepo.clear(keyToClear)

        title.value = ""; date.value = ""; time.value = ""; note.value = ""
        clothingType.value = ""; size.value = ""; brand.value = ""
        titleError.value = null; dateError.value = null; timeError.value = null
        typeError.value = null; sizeError.value = null; brandError.value = null

        autoSave.tryEmit(Unit)
    }

    fun submit(
        onOnlineSuccess: () -> Unit,
        onQueuedOffline: (String) -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        if (!validate()) { onError("Please fix the highlighted fields."); return@launch }

        val email = sessionEmail() ?: run { onError("No active session; please log in."); return@launch }
        val millis = parseDateTimeMillis(date.value, time.value)
            ?: run { onError("Date/Time must be yyyy-MM-dd and HH:mm."); return@launch }

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
            clearDraftAndResetForm()
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
            clearDraftAndResetForm()
            onQueuedOffline("No connectivity. Saved offline and will sync later.")
        }
    }

    fun onScheduleDonationSelected() {
        viewModelScope.launch { engagementRepo.logScheduleDonation() }
    }
}
