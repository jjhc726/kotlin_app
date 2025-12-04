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

@Composable
fun UsageFeaturesScreen(
    vm: UsageFeaturesViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val observer = remember { ConnectivityObserver(ctx) }
    val online by observer.onlineFlow().collectAsState(initial = observer.isOnlineNow())

    val selected by vm.selected.collectAsState()
    val why by vm.why.collectAsState()
    val submitting by vm.submitting.collectAsState()
    val submittedOk by vm.submittedOk.collectAsState()
    val least by vm.leastUsed.collectAsState()

    LaunchedEffect(Unit) { vm.loadLeastUsedThisWeek() }

    DisposableEffect(Unit) {
        onDispose { vm.persistDraftNow() }
    }

    var showResultBanner by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    var resultColor by remember { mutableStateOf(BannerColor.Info) }

    LaunchedEffect(submittedOk) {
        when (submittedOk) {
            true -> {
                showResultBanner = true
                resultText = "Thanks! Your feedback was sent."
                resultColor = BannerColor.Success
                kotlinx.coroutines.delay(900)
                showResultBanner = false
                vm.clearSubmittedFlag()
                goHome(ctx)
            }
            false -> {
                showResultBanner = true
                resultText = "No internet. Your feedback was saved and will be sent when you’re back online."
                resultColor = BannerColor.Warning
                kotlinx.coroutines.delay(1200)
                showResultBanner = false
                vm.clearSubmittedFlag()
                goHome(ctx)
            }
            null -> Unit
        }
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
            if (showResultBanner) SubmissionBanner(text = resultText, color = resultColor)

            HeaderTitle()
            Spacer(Modifier.height(16.dp))
            FeatureLeastUsedSection(least)
            Spacer(Modifier.height(24.dp))

            FeedbackCard(
                options = FeatureId.entries.toList(),
                selected = selected,
                why = why,
                submitting = submitting,
                onSelect = vm::onFeatureSelected,
                onWhyChange = vm::onWhyChanged,
                onSaveDraft = {
                    val ctxLocal = ctx
                    vm.saveDraftNow(
                        onSaved = { msg ->
                            android.widget.Toast.makeText(ctxLocal, msg, android.widget.Toast.LENGTH_LONG).show()
                        },
                        existingId = null
                    )
                },
                onSubmit = { fid, text -> vm.submitFeedback(fid, text) }
            )
        }
    }
}

private fun goHome(ctx: android.content.Context) {
    val act = ctx as? Activity
    ctx.startActivity(
        Intent(ctx, MainNavigationActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    )
    act?.finish()
}

@Composable
private fun SubmissionBanner(text: String, color: BannerColor) {
    val container = when (color) {
        BannerColor.Success -> MaterialTheme.colorScheme.primaryContainer
        BannerColor.Warning -> MaterialTheme.colorScheme.tertiaryContainer
        BannerColor.Info    -> MaterialTheme.colorScheme.secondaryContainer
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Text(text, modifier = Modifier.padding(12.dp))
    }
}

private enum class BannerColor { Success, Warning, Info }

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
        items.forEachIndexed { idx, item ->
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
    selected: FeatureId?,
    why: String,
    submitting: Boolean,
    onSelect: (FeatureId) -> Unit,
    onWhyChange: (String) -> Unit,
    onSaveDraft: () -> Unit,
    onSubmit: (FeatureId?, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SoftBlue),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Quick survey", color = DeepBlue, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
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
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { fid ->
                        DropdownMenuItem(text = { Text(fid.label) }, onClick = {
                            onSelect(fid); expanded = false
                        })
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = why,
                onValueChange = onWhyChange,
                label = { Text("Why should we improve it?") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = onSaveDraft,
                enabled = true
            ) { Text("Save draft") }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { onSubmit(selected, why) },
                enabled = (selected != null && why.isNotBlank() && !submitting)
            ) {
                if (submitting) CircularProgressIndicator() else Text("Send feedback")
            }
        }
    }
}
