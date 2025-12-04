package com.example.Recyclothes.ui.screens.drafts

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.Recyclothes.ui.screens.scheduledonation.ScheduleDonationActivity
import com.example.Recyclothes.ui.screens.usagefeatures.UsageFeaturesActivity
import com.example.Recyclothes.ui.theme.RecyclothesTheme

class DraftsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyclothesTheme {
                DraftsScreen(
                    onOpenScheduleDraft = { draftId ->
                        startActivity(
                            Intent(this, ScheduleDonationActivity::class.java)
                                .putExtra("DRAFT_ID", draftId)
                        )
                    },
                    onOpenUsageDraft = { draftId ->
                        startActivity(
                            Intent(this, UsageFeaturesActivity::class.java)
                                .putExtra("DRAFT_ID", draftId)
                        )
                    }
                )
            }
        }
    }
}
