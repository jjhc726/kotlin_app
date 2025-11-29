package com.example.Recyclothes.ui.screens.localdrafts

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.Recyclothes.data.local.DraftDonationEntity
import com.example.Recyclothes.data.repository.DonationRepository
import com.example.Recyclothes.ui.screens.quickDonation.QuickDonationActivity
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class LocalDraftsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RecyclothesTheme {
                LocalDraftsScreen(
                    repo = DonationRepository(this),
                    onBack = { finish() }
                )
            }
        }
    }
}
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = { Text("Local Drafts", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00838F))
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (drafts.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No drafts saved.")
                }
            } else {
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(drafts) { draft ->
                        DraftCard(
                            draft = draft,
                            onClick = {
                                val intent = Intent(context, QuickDonationActivity::class.java).apply {
                                    putExtra("draft_id", draft.id)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun DraftCard(
    draft: DraftDonationEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Description: ${draft.description}")
            Text("Type: ${draft.clothingType}")
            Text("Size: ${draft.size}")
            Text("Brand: ${draft.brand}")
            Text("Tags: ${draft.tags}")
            Text("User: ${draft.userEmail}")
        }
    }
}

