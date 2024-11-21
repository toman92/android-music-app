package com.example.tomansmusicplayerdb.ui.player

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Search and autoplay component at top of each screen
 *
 * @param searchQuery The search query
 * @param exoPlayerWrapper The ExoPlayer wrapper to control playback (used for repeat)
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchAndAutoPlay(searchQuery: MutableState<String>, exoPlayerWrapper: ExoPlayerWrapper) {
    val autoPlay = remember { mutableStateOf(false) }
    val repeat = remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            placeholder
            = { Text(text = "Search", style = MaterialTheme.typography.labelMedium) }
        )

        // Autoplay
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Auto Play",
                style = MaterialTheme.typography.labelSmall,
                color = if (autoPlay.value) Color.Green else Color.LightGray
            )
            Switch(
                checked = autoPlay.value,
                onCheckedChange = {
                    autoPlay.value = it
                    exoPlayerWrapper.setAutoPlay(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF006400), // Set the checked thumb color
                    checkedTrackColor = Color.Green // Set the checked track color
                )
            )
        }

        // Repeat
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Repeat",
                style = MaterialTheme.typography.labelSmall,
                color = if (repeat.value) Color.Green else Color.LightGray
            )
            Switch(
                checked = repeat.value,
                onCheckedChange = {
                    repeat.value = it
                    exoPlayerWrapper.setRepeat(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF006400), // Set the checked thumb color
                    checkedTrackColor = Color.Green // Set the checked track color
                )
            )
        }
    }
}