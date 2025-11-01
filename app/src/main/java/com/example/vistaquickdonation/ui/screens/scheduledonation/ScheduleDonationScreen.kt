package com.example.vistaquickdonation.ui.screens.scheduledonation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.ui.theme.SoftBlue
import com.example.vistaquickdonation.viewmodel.ScheduleDonationViewModel
import android.content.Intent
import com.example.vistaquickdonation.ui.screens.home.HomePageActivity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDonationDesign(
    vm: ScheduleDonationViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftBlue)
            .verticalScroll(scroll)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Schedule Your Donation", fontSize = 26.sp, color = DeepBlue)
        Text("Plan ahead when you want to deliver your items.", fontSize = 14.sp, color = DeepBlue.copy(.75f))
        Spacer(Modifier.height(24.dp))


        OutlinedTextField(
            value = vm.title.value,
            onValueChange = { vm.title.value = it },
            label = { Text("Donation Title (Required)") },
            supportingText = { vm.titleError.value?.let { Text(it, color = Color.Red) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = vm.date.value,
            onValueChange = { vm.date.value = it },
            label = { Text("Scheduled Donation Date (yyyy-MM-dd)") },
            supportingText = { vm.dateError.value?.let { Text(it, color = Color.Red) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = vm.time.value,
            onValueChange = { vm.time.value = it },
            label = { Text("Scheduled Donation Time (HH:mm)") },
            supportingText = { vm.timeError.value?.let { Text(it, color = Color.Red) } },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)

        )
        Spacer(Modifier.height(12.dp))


        var expandType by remember { mutableStateOf(false) }
        val types = listOf("Shirt","Pants","Jacket","Dress","Shoes","Accessories")
        ExposedDropdownMenuBox(expanded = expandType, onExpandedChange = { expandType = !expandType }) {
            OutlinedTextField(
                value = vm.clothingType.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Clothing Type (Required)") },
                supportingText = { vm.typeError.value?.let { Text(it, color = Color.Red) } },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandType) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandType, onDismissRequest = { expandType = false }) {
                types.forEach { t ->
                    DropdownMenuItem(text = { Text(t) }, onClick = {
                        vm.clothingType.value = t; expandType = false
                    })
                }
            }
        }
        Spacer(Modifier.height(12.dp))


        var expandSize by remember { mutableStateOf(false) }
        val sizes = listOf("XS","S","M","L","XL","XXL")
        ExposedDropdownMenuBox(expanded = expandSize, onExpandedChange = { expandSize = !expandSize }) {
            OutlinedTextField(
                value = vm.size.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Size (Required)") },
                supportingText = { vm.sizeError.value?.let { Text(it, color = Color.Red) } },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandSize) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandSize, onDismissRequest = { expandSize = false }) {
                sizes.forEach { s ->
                    DropdownMenuItem(text = { Text(s) }, onClick = {
                        vm.size.value = s; expandSize = false
                    })
                }
            }
        }
        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = vm.brand.value,
            onValueChange = { vm.brand.value = it },
            label = { Text("Brand (Required)") },
            supportingText = { vm.brandError.value?.let { Text(it, color = Color.Red) } },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = vm.note.value,
            onValueChange = { vm.note.value = it },
            label = { Text("Additional Notes") },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            maxLines = 5
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                vm.submit(
                    onOnlineSuccess = {
                        Toast.makeText(ctx, "Scheduled online successfully", Toast.LENGTH_LONG).show()
                        ctx.startActivity(Intent(ctx, HomePageActivity::class.java))
                        (ctx as? Activity)?.finish()
                    },
                    onQueuedOffline = { msg ->
                        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                        ctx.startActivity(Intent(ctx, HomePageActivity::class.java))
                        (ctx as? Activity)?.finish()
                    },
                    onError = { err ->
                        Toast.makeText(ctx, err, Toast.LENGTH_LONG).show()
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = DeepBlue, contentColor = Color.White),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Confirm Schedule")
        }
        Spacer(Modifier.height(12.dp))
    }
}
