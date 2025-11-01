package com.example.Recyclothes.ui.screens.quickDonation

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Recyclothes.ui.screens.home.HomePageActivity
import com.example.Recyclothes.ui.theme.DeepBlue
import com.example.Recyclothes.viewmodel.DonationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DonationSubmitButton(viewModel: DonationViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            viewModel.uploadDonation { success, message ->
                scope.launch {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                    if (success) {
                        delay(1000)
                        val intent = Intent(context, HomePageActivity::class.java)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = DeepBlue,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Submit Donation", color = Color.White, fontSize = 18.sp)
    }
}
