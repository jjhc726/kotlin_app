package com.example.Recyclothes.ui.screens.drafts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.connectivity.ConnectivityBanner
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.viewmodel.DraftsViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun DraftsScreen(
    vm: DraftsViewModel = viewModel(),
    onOpenScheduleDraft: (String) -> Unit,
    onOpenUsageDraft: (String) -> Unit
) {
    val sched by vm.sched.collectAsState()
    val usage by vm.usage.collectAsState()

    val ctx = LocalContext.current
    val observer = remember { ConnectivityObserver(ctx) }
    val online by observer.onlineFlow().collectAsState(initial = observer.isOnlineNow())

    Surface(color = SoftBlue) {
        Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
            ConnectivityBanner(online = online)
            Spacer(Modifier.height(12.dp))

            Text("Schedule Donation Drafts", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            if (sched.isEmpty()) {
                Text("No drafts yet.")
            } else {
                sched.forEach {
                    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(it.title, style = MaterialTheme.typography.titleMedium)
                            Text("${it.date} ${it.time}")
                            Spacer(Modifier.height(8.dp))
                            Row {
                                Button(onClick = { onOpenScheduleDraft(it.draftId) }) {
                                    Text("Open")
                                }
                                Spacer(Modifier.width(8.dp))
                                OutlinedButton(onClick = { vm.deleteScheduleDraft(it.draftId) }) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(20.dp))

            Text("Features Usage Drafts", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            if (usage.isEmpty()) {
                Text("No drafts yet.")
            } else {
                usage.forEach {
                    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(it.featureName, style = MaterialTheme.typography.titleMedium)
                            Text(it.why)
                            Spacer(Modifier.height(8.dp))
                            Row {
                                Button(onClick = { onOpenUsageDraft(it.draftId) }) {
                                    Text("Open")
                                }
                                Spacer(Modifier.width(8.dp))
                                OutlinedButton(onClick = { vm.deleteUsageDraft(it.draftId) }) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
