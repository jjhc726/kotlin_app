package com.example.Recyclothes.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.remote.AppRatingDraft
import com.example.Recyclothes.data.repository.AppRatingDraftRepository
import com.example.Recyclothes.data.repository.AppRatingRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AppRatingViewModel(app: Application) : AndroidViewModel(app) {

    val stars     = mutableStateOf(0)
    val likeMost  = mutableStateOf("")
    val comments  = mutableStateOf("")
    val recommend = mutableStateOf<Boolean?>(null)

    private val _improvements = mutableStateListOf<String>()
    val improvements: List<String> get() = _improvements

    fun toggleImprovement(s: String) {
        if (_improvements.contains(s)) _improvements.remove(s) else _improvements.add(s)
        onFieldEdited()
    }

    private val draftRepo = AppRatingDraftRepository(app)
    private val autoSave = MutableStateFlow(Unit)

    private val onlineRepo = AppRatingRepository()
    val submitting = mutableStateOf(false)

    init {
        viewModelScope.launch {
            draftRepo.load(draftKey())?.let { d ->
                stars.value = d.stars
                likeMost.value = d.likeMost
                comments.value = d.comments
                recommend.value = d.recommend
                _improvements.clear()
                _improvements.addAll(d.improvements)
            }
        }
        setupAutosave()
    }

    private fun sessionEmail(): String? =
        FirebaseAuth.getInstance().currentUser?.email
            ?: getApplication<Application>()
                .getSharedPreferences("session", Context.MODE_PRIVATE)
                .getString("email", null)
                ?.trim()?.lowercase()

    private fun draftKey(): String = sessionEmail() ?: "anonymous"

    fun onFieldEdited() { autoSave.tryEmit(Unit) }

    @OptIn(FlowPreview::class)
    private fun setupAutosave() {
        autoSave.debounce(400).onEach {
            persistDraftNow()
        }.launchIn(viewModelScope)
    }

    fun persistDraftNow() {
        viewModelScope.launch {
            draftRepo.save(
                draftKey(),
                AppRatingDraft(
                    stars = stars.value,
                    likeMost = likeMost.value,
                    improvements = _improvements.toList(),
                    recommend = recommend.value,
                    comments = comments.value
                )
            )
        }
    }

    fun clearDraftAndReset() {
        viewModelScope.launch {
            draftRepo.clear(draftKey())
            stars.value = 0
            likeMost.value = ""
            comments.value = ""
            recommend.value = null
            _improvements.clear()
            autoSave.tryEmit(Unit)
        }
    }

    fun submit(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            submitting.value = true
            val ok = onlineRepo.submit(
                email = sessionEmail(),
                draft = AppRatingDraft(
                    stars = stars.value,
                    likeMost = likeMost.value,
                    improvements = _improvements.toList(),
                    recommend = recommend.value,
                    comments = comments.value
                )
            )
            if (ok) clearDraftAndReset()
            submitting.value = false
            onResult(ok)
        }
    }
}
