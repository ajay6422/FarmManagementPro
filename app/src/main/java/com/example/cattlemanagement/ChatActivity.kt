package com.example.cattlemanagement

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cattlemanagement.databinding.ActivityChatBinding
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private val viewModel: ChatViewModel by viewModels()

    private val requestAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startVoiceInput()
            } else {
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val speechToTextLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                if (!results.isNullOrEmpty()) {
                    binding.etMessage.setText(results[0])
                    binding.etMessage.setSelection(binding.etMessage.text?.length ?: 0)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatAdapter(mutableListOf()) { position ->
            viewModel.translateMessageToHindi(position)
        }

        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerViewChat.adapter = adapter

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }

        binding.btnMic.setOnClickListener {
            checkMicPermissionAndStartVoice()
        }

        viewModel.messages.observe(this) { messages ->
            adapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.recyclerViewChat.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun checkMicPermissionAndStartVoice() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceInput()
            }

            else -> {
                requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your cattle question")
        }

        try {
            speechToTextLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Speech recognition not available: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}