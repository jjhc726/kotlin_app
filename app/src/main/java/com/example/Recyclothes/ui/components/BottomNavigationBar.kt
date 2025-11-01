package com.example.Recyclothes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Recyclothes.ui.navigation.BottomNavItem
import com.example.Recyclothes.ui.theme.MediumBlue
import com.example.Recyclothes.ui.theme.TealMedium

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem

            NavigationBarItem(
                selected = isSelected,
                onClick = {},
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onItemSelected(item)
                            }
                    ) {
                        androidx.compose.foundation.layout.Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .width(50.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) MediumBlue else Color.Transparent
                                )
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) Color.White else Color.Black
                            )
                        }

                        Text(
                            text = item.label,
                            color = if (isSelected) TealMedium else Color.Black,
                            fontSize = 13.sp
                        )
                    }
                },
                label = null,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Black,
                    selectedTextColor = TealMedium,
                    unselectedTextColor = Color.Black
                )
            )
        }
    }
}
