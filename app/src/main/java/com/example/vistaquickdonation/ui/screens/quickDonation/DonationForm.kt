package com.example.vistaquickdonation.ui.screens.quickDonation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.viewmodel.DonationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationForm(viewModel: DonationViewModel) {
    OutlinedTextField(
        value = viewModel.description.value,
        onValueChange = {
            if (it.length <= 200) viewModel.description.value = it
        },
        label = { Text("Description (Required)", color = DeepBlue.copy(alpha = 0.6f)) },
        supportingText = {
            viewModel.descriptionError.value?.let { Text(it, color = Color.Red) }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        maxLines = 5,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DeepBlue,
            unfocusedBorderColor = DeepBlue,
            focusedTextColor = DeepBlue,
            unfocusedTextColor = DeepBlue,
            cursorColor = DeepBlue,
            errorBorderColor = DeepBlue
        ),
        placeholder = { Text("Write a brief description...", color = DeepBlue.copy(alpha = 0.5f)) }
    )

    Spacer(modifier = Modifier.height(12.dp))

    var expandedType by remember { mutableStateOf(false) }
    val clothingOptions = listOf("Shirt", "Pants", "Jacket", "Dress", "Shoes", "Accessories")

    ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = !expandedType }) {
        OutlinedTextField(
            value = viewModel.clothingType.value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Clothing Type (Required)", color = DeepBlue.copy(alpha = 0.6f)) },
            supportingText = {
                viewModel.clothingTypeError.value?.let { Text(it, color = Color.Red) }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DeepBlue,
                unfocusedBorderColor = DeepBlue,
                focusedTextColor = DeepBlue,
                unfocusedTextColor = DeepBlue,
                cursorColor = DeepBlue,
                errorBorderColor = DeepBlue
            )
        )

        ExposedDropdownMenu(
            expanded = expandedType,
            onDismissRequest = { expandedType = false },
            modifier = Modifier.background(Color.White)
        ) {
            clothingOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = DeepBlue) },
                    onClick = {
                        viewModel.clothingType.value = option
                        expandedType = false
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    var expandedSize by remember { mutableStateOf(false) }
    val sizeOptions = listOf("XS", "S", "M", "L", "XL", "XXL")

    ExposedDropdownMenuBox(expanded = expandedSize, onExpandedChange = { expandedSize = !expandedSize }) {
        OutlinedTextField(
            value = viewModel.size.value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Size (Required)", color = DeepBlue.copy(alpha = 0.6f)) },
            supportingText = {
                viewModel.sizeError.value?.let { Text(it, color = Color.Red) }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSize) },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DeepBlue,
                unfocusedBorderColor = DeepBlue,
                focusedTextColor = DeepBlue,
                unfocusedTextColor = DeepBlue,
                cursorColor = DeepBlue,
                errorBorderColor = DeepBlue
            )
        )

        ExposedDropdownMenu(
            expanded = expandedSize,
            onDismissRequest = { expandedSize = false },
            modifier = Modifier.background(Color.White)
        ) {
            sizeOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = DeepBlue) },
                    onClick = {
                        viewModel.size.value = option
                        expandedSize = false
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = viewModel.brand.value,
        onValueChange = { if (it.length <= 40) viewModel.brand.value = it },
        label = { Text("Brand (Required)", color = DeepBlue.copy(alpha = 0.6f)) },
        supportingText = { viewModel.brandError.value?.let { Text(it, color = Color.Red) } },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DeepBlue,
            unfocusedBorderColor = DeepBlue,
            focusedTextColor = DeepBlue,
            unfocusedTextColor = DeepBlue,
            cursorColor = DeepBlue,
            errorBorderColor = DeepBlue
        )
    )
}
