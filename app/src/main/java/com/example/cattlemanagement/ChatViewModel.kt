package com.example.cattlemanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = GeminiRepository()

    private val _messages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val messages: LiveData<MutableList<ChatMessage>> = _messages

    fun sendMessage(userMessage: String) {
        val currentList = _messages.value ?: mutableListOf()
        currentList.add(ChatMessage(userMessage, true))
        _messages.value = currentList

        viewModelScope.launch {
            val reply = repository.sendMessage(userMessage)
            val updatedList = _messages.value ?: mutableListOf()
            updatedList.add(ChatMessage(reply, false))
            _messages.postValue(updatedList)
        }
    }

    fun translateMessageToHindi(position: Int) {
        val currentList = _messages.value ?: return
        val message = currentList[position]

        if (message.isUser) return
        if (!message.hindiText.isNullOrEmpty()) return

        viewModelScope.launch {
            val hindi = repository.translateToHindi(message.text)
            message.hindiText = hindi
            _messages.postValue(currentList)
        }
    }
}