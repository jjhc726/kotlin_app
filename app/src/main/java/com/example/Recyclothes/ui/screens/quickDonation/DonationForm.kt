package com.example.Recyclothes.ui.screens.quickDonation

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.viewmodel.DonationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationForm(viewModel: DonationViewModel) {

    // DESCRIPTION
    val description by viewModel.description.collectAsState()

    OutlinedTextField(
        value = description,
        onValueChange = {
            if (it.length <= 200) viewModel.updateDescription(it)
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

    // CLOTHING TYPE
    var expandedType by remember { mutableStateOf(false) }
    val clothingOptions = listOf("Shirt", "Pants", "Jacket", "Dress", "Shoes", "Accessories")
    val clothingType by viewModel.clothingType.collectAsState()

    ExposedDropdownMenuBox(
        expanded = expandedType,
        onExpandedChange = { expandedType = !expandedType }
    ) {
        OutlinedTextField(
            value = clothingType,
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
                        viewModel.updateClothingType(option)
                        expandedType = false
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // SIZE
    var expandedSize by remember { mutableStateOf(false) }
    val sizeOptions = listOf("XS", "S", "M", "L", "XL", "XXL")
    val size by viewModel.size.collectAsState()

    ExposedDropdownMenuBox(
        expanded = expandedSize,
        onExpandedChange = { expandedSize = !expandedSize }
    ) {
        OutlinedTextField(
            value = size,
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
                        viewModel.updateSize(option)
                        expandedSize = false
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // BRAND
    val brand by viewModel.brand.collectAsState()

    OutlinedTextField(
        value = brand,
        onValueChange = { if (it.length <= 40) viewModel.updateBrand(it) },
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