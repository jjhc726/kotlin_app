package com.example.vistaquickdonation.ui.screens.quickDonation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vistaquickdonation.ui.theme.DeepBlue
import com.example.vistaquickdonation.ui.theme.TealDark
import com.example.vistaquickdonation.viewmodel.DonationViewModel

@Composable
fun DonationTagsSection(viewModel: DonationViewModel) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        viewModel.availableTags.forEach { tag ->
            val isSelected = viewModel.selectedTags.value.contains(tag)
            Text(
                text = tag,
                color = if (isSelected) Color.White else TealDark,
                fontSize = 14.sp,
                modifier = Modifier
                    .background(
                        color = if (isSelected) DeepBlue else Color.White,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(1.dp, DeepBlue, RoundedCornerShape(6.dp))
                    .clickable { viewModel.toggleTag(tag) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}