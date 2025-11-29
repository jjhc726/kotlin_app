package com.example.Recyclothes.ui.screens.localdrafts

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.Recyclothes.data.local.DraftDonationEntity
import com.example.Recyclothes.data.repository.DonationRepository
import com.example.Recyclothes.ui.screens.quickDonation.QuickDonationActivity
import com.example.Recyclothes.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDraftsScreen(
    repo: DonationRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var drafts by remember { mutableStateOf<List<DraftDonationEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        drafts = repo.getAllDrafts()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Local Drafts", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TealDark
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(SoftBlue)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            if (drafts.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No drafts saved.", color = DeepBlue)
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(drafts) { draft ->
                        DraftCard(
                            draft = draft,
                            onClick = {
                                val intent = Intent(context, QuickDonationActivity::class.java)
                                intent.putExtra("draft_id", draft.id)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}
