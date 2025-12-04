package com.example.Recyclothes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Recyclothes.data.model.Message
import com.example.Recyclothes.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadMessages(charityName: String, userEmail: String) {
        viewModelScope.launch {
            repository.getMessagesFlow(charityName, userEmail).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun sendMessage(charityName: String, userEmail: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(charityName, userEmail, text)
        }
    }
}
