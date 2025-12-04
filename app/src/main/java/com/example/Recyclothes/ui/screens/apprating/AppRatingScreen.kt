package com.example.Recyclothes.ui.screens.apprating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Recyclothes.connectivity.ConnectivityBanner
import com.example.Recyclothes.connectivity.ConnectivityObserver
import com.example.Recyclothes.ui.theme.SoftBlue
import com.example.Recyclothes.viewmodel.AppRatingViewModel

@Composable
fun AppRatingScreen(vm: AppRatingViewModel = viewModel()) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val observer = remember { ConnectivityObserver(ctx) }
    val online by observer.onlineFlow().collectAsState(initial = observer.isOnlineNow())

    DisposableEffect(lifecycleOwner) {
        val observerLf = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) vm.persistDraftNow()
        }
        lifecycleOwner.lifecycle.addObserver(observerLf)
        onDispose {
            vm.persistDraftNow()
            lifecycleOwner.lifecycle.removeObserver(observerLf)
        }
    }

    Surface(color = SoftBlue) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(SoftBlue),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConnectivityBanner(online = online)

            Text("Rate Recyclothes", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            StarRating(
                value = vm.stars.value,
                onChange = { vm.stars.value = it; vm.onFieldEdited() }
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = vm.likeMost.value,
                onValueChange = { vm.likeMost.value = it; vm.onFieldEdited() },
                label = { Text("What did you like most?") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text("What should we improve?")
            Spacer(Modifier.height(8.dp))
            ImprovementsChecklist(
                selected = vm.improvements,
                onToggle = { vm.toggleImprovement(it) }
            )

            Spacer(Modifier.height(16.dp))
            Text("Would you recommend the app?")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = vm.recommend.value == true,
                    onClick = { vm.recommend.value = true; vm.onFieldEdited() }
                ); Text("Yes")
                Spacer(Modifier.width(16.dp))
                RadioButton(
                    selected = vm.recommend.value == false,
                    onClick = { vm.recommend.value = false; vm.onFieldEdited() }
                ); Text("No")
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = vm.comments.value,
                onValueChange = { vm.comments.value = it; vm.onFieldEdited() },
                label = { Text("Additional comments") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    vm.submit { ok ->
                        if (ok) {
                            android.widget.Toast
                                .makeText(ctx, "Thanks for your rating!", android.widget.Toast.LENGTH_LONG)
                                .show()
                        } else {
                            val msg = if (online) "There was a problem sending your rating."
                            else "No internet. Please try again later."
                            android.widget.Toast.makeText(ctx, msg, android.widget.Toast.LENGTH_LONG).show()
                        }
                    } },
                enabled = !vm.submitting.value
            ) {
                if (vm.submitting.value) CircularProgressIndicator() else Text("Submit")
            }
        }
    }
}

@Composable
private fun StarRating(value: Int, onChange: (Int) -> Unit) {
    val yellow = Color(0xFFFFC107)
    Row(verticalAlignment = Alignment.CenterVertically) {
        (1..5).forEach { i ->
            IconButton(onClick = { onChange(i) }) {
                if (i <= value)
                    Icon(Icons.Filled.Star, contentDescription = "$i", tint = yellow)
                else
                    Icon(Icons.Outlined.Star, contentDescription = "$i", tint = Color.Gray)
            }
        }
        Spacer(Modifier.width(8.dp))
        Text("$value / 5")
    }
}

@Composable
private fun ImprovementsChecklist(
    selected: List<String>,
    onToggle: (String) -> Unit
) {
    val opts = listOf("Performance", "Design", "Onboarding", "Stability", "Notifications")
    Column(Modifier.fillMaxWidth()) {
        opts.forEach { label ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selected.contains(label),
                    onCheckedChange = { onToggle(label) }
                )
                Text(label)
            }
        }
    }
}
