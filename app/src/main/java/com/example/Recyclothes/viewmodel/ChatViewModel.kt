package com.example.Recyclothes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.model.Message
import com.example.Recyclothes.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadMessages(charityName: String, userEmail: String) {
        viewModelScope.launch {
            repository.getMessagesFlow(charityName, userEmail).collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(charityName: String, userEmail: String, text: String, isOnline: Boolean) {
        viewModelScope.launch {

            val tempMessage = Message(
                id = System.currentTimeMillis().toString(),
                fromUserId = userEmail,
                toCharityId = charityName,
                text = text,
                timestamp = System.currentTimeMillis(),
                status = if (isOnline) "sending" else "error" // ⏳ u ⚠
            )

            _messages.value = _messages.value + tempMessage

            val finalMessage = repository.sendMessage(
                charityName,
                userEmail,
                text,
                isOnline
            )

            _messages.value = _messages.value.map {
                if (it.id == tempMessage.id) finalMessage else it
            }
        }
    }
    fun retryPending() {
        viewModelScope.launch {
            repository.resendPendingMessages()
        }
    }
}

