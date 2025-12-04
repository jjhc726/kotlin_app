package com.example.Recyclothes.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.connectivity.ConnectivityBanner
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.model.Message
import com.example.Recyclothes.viewmodel.ChatViewModel
import com.example.Recyclothes.viewmodel.ChatViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    charityName: String,
    userId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ChatViewModel =
        viewModel(factory = ChatViewModelFactory(context))
    val observer = remember { ConnectivityObserver(context) }

    val online by observer.onlineFlow().collectAsState(initial = observer.isOnlineNow())

    LaunchedEffect(charityName, userId) {
        viewModel.loadMessages(charityName, userId)
    }

    LaunchedEffect(online) {
        if (online) {
            viewModel.retryPending()
        }
    }

    val messages by viewModel.messages.collectAsState()
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat con $charityName") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            ConnectivityBanner(online)

            LazyColumn(
                modifier = Modifier.weight(1f).padding(10.dp)
            ) {
                items(messages) { msg ->
                    MessageBubble(msg = msg, isMine = msg.fromUserId == userId)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") }
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (text.isNotBlank()) {

                            viewModel.sendMessage(
                                charityName,
                                userId,
                                text,
                                isOnline = online
                            )
                            text = ""
                        }
                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: Message, isMine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(horizontalAlignment = if (isMine) Alignment.End else Alignment.Start) {

            Surface(
                color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(6.dp)
            ) {
                Text(
                    msg.text,
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (isMine) {
                Text(
                    text =
                        when (msg.status) {
                            "sending" -> "⏳"
                            "sent" -> "✓"
                            "error" -> "⚠ Error"
                            else -> ""
                        },
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
