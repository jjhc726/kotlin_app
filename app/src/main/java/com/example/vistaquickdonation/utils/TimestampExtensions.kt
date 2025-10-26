package com.example.vistaquickdonation.utils

import com.example.vistaquickdonation.data.model.AppNotification
import com.example.vistaquickdonation.data.model.DonationItem
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun Timestamp.toPretty(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(this.toDate())
}

fun DonationItem.toNotification(): AppNotification {
    val millis = (createdAt ?: Timestamp.now()).toDate().time
    val whenText = (createdAt ?: Timestamp.now()).toPretty()
    val title = "Donation successful"
    val body = buildString {
        append(description.ifBlank { clothingType.ifBlank { "Clothes" } })
        append(" — ")
        append(whenText)
    }
    return AppNotification(title, body, millis)
}
