package com.example.Recyclothes.ui.screens.UsageFeatures

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.connectivity.ConnectivityBanner
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.data.model.FeatureId
import com.example.Recyclothes.ui.screens.main.MainNavigationActivity
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.ui.theme.MediumBlue
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.ui.theme.TealDark
import com.example.Recyclothes.viewmodel.LeastUsedUi
import com.example.Recyclothes.viewmodel.UsageFeaturesViewModel
import com.example.Recyclothes.viewmodel.FeaturesUsageViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UsageFeaturesScreen(
    analyticsVm: UsageFeaturesViewModel = viewModel(),
    eventsVm: FeaturesUsageViewModel = viewModel()
) {
    LaunchedEffect(Unit) { analyticsVm.loadLeastUsedThisWeek() }

    val least by analyticsVm.leastUsed.collectAsState()
    val ctx = LocalContext.current

    val observer = remember { ConnectivityObserver(ctx) }
    val online by observer.onlineFlow().collectAsState(initial = observer.isOnlineNow())

    var submitting by remember { mutableStateOf(false) }
    var submittedOk by remember { mutableStateOf<Boolean?>(null) }
    var submittedMsg by remember { mutableStateOf("") }

    fun goHome() {
        val act = ctx as? Activity
        ctx.startActivity(
            Intent(ctx, MainNavigationActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        act?.finish()
    }

    Surface(color = SoftBlue) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConnectivityBanner(online = online)
            HeaderTitle()

            Spacer(Modifier.height(16.dp))

            FeatureLeastUsedSection(least)

            Spacer(Modifier.height(24.dp))

            val featureOptions: List<FeatureId> =
                if (least.isNotEmpty()) least.map { it.featureId } else FeatureId.entries.toList()

            FeedbackCard(
                options = featureOptions,
                submitting = submitting,
                submittedOk = submittedOk,
                onSubmit = { fid, why ->
                    submitting = true
                    val email = FirebaseAuth.getInstance().currentUser?.email ?: "anonymous@local"
                    eventsVm.submitFeedback(
                        userEmail = email,
                        featureName = fid.label,
                        why = why,
                        onOnlineOk = {
                            submitting = false
                            submittedOk = true
                            submittedMsg = "Thanks! Your feedback was sent."
                        },
                        onQueuedOffline = {
                            submitting = false
                            submittedOk = true
                            submittedMsg = "Saved offline. It will be sent when you’re back online."
                        }
                    )
                },
                onDismissSubmitted = {
                    submittedOk = null
                    submittedMsg = ""
                },
                onNavigateHome = { goHome() },
                customMessage = submittedMsg
            )
        }
    }
}

@Composable
private fun HeaderTitle() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = TealDark),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier.padding(vertical = 18.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "The features least used are:",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "We track weekly usage to identify where to improve.",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun FeatureLeastUsedSection(items: List<LeastUsedUi>) {
    if (items.isEmpty()) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No data yet for this week.", color = MediumBlue)
        }
    } else {
        items.take(3).forEachIndexed { idx, item ->
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SoftBlue),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "${idx + 1}. ${item.label}",
                        color = DeepBlue,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${item.weeklyCount} uses this week",
                        color = DeepBlue.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedbackCard(
    options: List<FeatureId>,
    submitting: Boolean,
    submittedOk: Boolean?,
    onSubmit: (FeatureId, String) -> Unit,
    onDismissSubmitted: () -> Unit,
    onNavigateHome: () -> Unit,
    customMessage: String
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(options.firstOrNull()) }
    var why by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftBlue),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Quick survey",
                color = DeepBlue,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text("Which feature should we improve, and why?")

            Spacer(Modifier.height(14.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selected?.label ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select a feature") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { fid ->
                        DropdownMenuItem(
                            text = { Text(fid.label) },
                            onClick = {
                                selected = fid
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = why,
                onValueChange = { why = it },
                label = { Text("Why should we improve it?") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { selected?.let { onSubmit(it, why) } },
                enabled = selected != null && why.isNotBlank() && !submitting
            ) {
                if (submitting) CircularProgressIndicator() else Text("Send feedback")
            }

            when (submittedOk) {
                true -> {
                    Spacer(Modifier.height(8.dp))
                    Text(customMessage, color = DeepBlue)
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(900)
                        onDismissSubmitted()
                        onNavigateHome()
                    }
                }
                false -> {
                    Spacer(Modifier.height(8.dp))
                    Text("We couldn’t send it. Please try again.", color = DeepBlue)
                }
                null -> Unit
            }
        }
    }
}
