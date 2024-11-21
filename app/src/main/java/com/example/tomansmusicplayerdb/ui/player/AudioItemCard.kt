package com.example.tomansmusicplayerdb.ui.player

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.tomansmusicplayerdb.data.local.database.Playlist
import com.example.tomansmusicplayerdb.ui.model.PlaylistItemViewModel
import com.example.tomansmusicplayerdb.ui.model.PlaylistUiState
import com.example.tomansmusicplayerdb.ui.model.PlaylistViewModel
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

/**
 * Card component displaying controls for an audio file
 */
@OptIn(UnstableApi::class)
@Composable
fun AudioItemCard(
    audioFiles: MutableList<AudioFile>,
    audioFile: AudioFile,
    exoPlayerWrapper: ExoPlayerWrapper,
    addFavourite: ((name: String) -> Unit)? = null,
    deleteFavourite: ((name: String) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val playbackSpeed = remember { mutableFloatStateOf(1.0f) }
    val expanded = remember { mutableStateOf(false) }
    val currentPosition = remember { mutableLongStateOf(0L) }

    // Values used for buttons to adjust speed
    val speedList: List<Float> = listOf(0.6f, 0.65f, 0.7f, 0.75f, 0.8f, 0.85f, 0.9f, 0.95f, 1.0f)

    // Update current position every half second
    LaunchedEffect(exoPlayerWrapper) {
        while (true) {
            if (audioFile == exoPlayerWrapper.getCurrentPlayingFile()) {
                currentPosition.longValue =
                    exoPlayerWrapper.getExoPlayer()?.currentPosition ?: 0L
            }
            delay(500) // half second in milliseconds.
        }
    }

    // playlist and playlist item view models to get/add from/to DB
    val viewModel: PlaylistViewModel = hiltViewModel()
    val itemViewModel: PlaylistItemViewModel = hiltViewModel()

    // Get saved playlists
    val savedPlaylists by viewModel.uiState.collectAsStateWithLifecycle()
    var data = remember { listOf<Playlist>() }
    if (savedPlaylists is PlaylistUiState.Success) {
        data = (savedPlaylists as PlaylistUiState.Success).data
    }

    // Get playlist items
    val savedPlaylistItems = itemViewModel.getAllPlaylistItems().collectAsStateWithLifecycle(
        initialValue = emptyList(),
        lifecycle = lifecycleOwner.lifecycle
    ).value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Title: ${audioFile.title}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Artist: ${audioFile.artist}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Album: ${audioFile.album}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Dynamically display Waveform Visualizer based on audio file playing state
        if (exoPlayerWrapper.getExoPlayer()?.audioSessionId != null && audioFile == exoPlayerWrapper.getCurrentPlayingFile()) {
            WaveformVisualizer(
                exoPlayerWrapper.getExoPlayer()?.audioSessionId,
                exoPlayerWrapper
            )
        } else {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }

        Row(
            Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column {
                Text(
                    // See below for formatTime function
                    text = "${formatTime(currentPosition.longValue)} / ${
                        formatTime(audioFile.duration)
                    }"
                )
                Slider(
                    colors = if (exoPlayerWrapper.getIsPlaying() && audioFile == exoPlayerWrapper.getCurrentPlayingFile()) SliderColors(
                        Color.Green,
                        Color.Green,
                        MaterialTheme.colorScheme.onSurface,
                        Color.Transparent,
                        Color.Green,
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.colorScheme.onSurface,
                    ) else SliderDefaults.colors(),
                    value = if (audioFile == exoPlayerWrapper.getCurrentPlayingFile()) currentPosition.longValue.toFloat() else 0f,//currentPosition.value.toFloat(),
                    onValueChange = {
                        // Update current position as file is playing or when slider is moved
                        if (audioFile == exoPlayerWrapper.getCurrentPlayingFile())
                            currentPosition.longValue = it.toLong()
                    },
                    valueRange = 0f..audioFile.duration.toFloat(),
                    onValueChangeFinished = {
                        // To handle user sliding the slider. Moves to that position in the audio file.
                        if (audioFile == exoPlayerWrapper.getCurrentPlayingFile())
                            exoPlayerWrapper.getExoPlayer()?.seekTo(currentPosition.longValue)
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Dynamically display play/pause icon based on isPlaying and currentPlayingFile
            IconButton(onClick = { onClick?.invoke() }) {
                Icon(
                    imageVector = if (exoPlayerWrapper.getIsPlaying() && audioFile == exoPlayerWrapper.getCurrentPlayingFile()) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (exoPlayerWrapper.getIsPlaying() && audioFile == exoPlayerWrapper.getCurrentPlayingFile()) "Pause" else "Play"
                )
            }
            Spacer(modifier = Modifier.padding(16.dp))
            // Stop button
            IconButton(onClick = { exoPlayerWrapper.stop(audioFile) }) {
                Icon(imageVector = Icons.Filled.Stop, contentDescription = "Stop")
            }
            Spacer(modifier = Modifier.padding(16.dp))
            // Favourite button
            if (addFavourite != null && deleteFavourite != null) {
                IconButton(onClick = {
                    // See below for toggleFavourite function
                    toggleFavorite(
                        audioFile,
                        audioFiles,
                        addFavourite,
                        deleteFavourite
                    )
                }) {
                    Icon(
                        tint = if (audioFile.isFavorite) Color.Green else LocalContentColor.current,
                        imageVector = if (audioFile.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (audioFile.isFavorite) "Favourite" else "Not Favourite"
                    )
                }
            }
        }

        // Expandable Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { expanded.value = !expanded.value }, // Toggle expanded state on click
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("More Options")
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (expanded.value) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded.value) "Collapse" else "Expand"
            )
        }

        // Content to be shown when expanded
        if (expanded.value) {
            HorizontalDivider(
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
                thickness = 5.dp,
                color = Color.Green
            )

            // Speed Settings
            Column(modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp)) {
                Row(
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = "Speed",
                        tint = Color.Green,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp))
                    Text(
                        "Speed Settings".uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Green
                    )
                }
                Row(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)) {
                    Text(
                        "Current Speed: ${roundFloatToTwoDecimals(playbackSpeed.floatValue)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                // Speed buttons for easy setting of playback speed
                LazyRow(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)) {
                    items(speedList) {
                        Button(
                            modifier = Modifier.size(75.dp, 30.dp),
                            onClick = { ->
                                playbackSpeed.floatValue = it
                                exoPlayerWrapper.setSpeed(playbackSpeed.floatValue)
                            },
                        ) {
                            Text("$it", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Speed slider for more precise setting  of playback speed
                Slider(
                    value = roundFloatToTwoDecimals(exoPlayerWrapper.getSpeed()),
                    onValueChange = {
                        playbackSpeed.floatValue =
                            it.coerceIn(0.5f, 2.0f) // Limit speed between 0.5x and 2x
                    },
                    valueRange = 0.25f..2.0f, // Speed range from 0.5x to 2x
                    onValueChangeFinished = {
                        exoPlayerWrapper.setSpeed(playbackSpeed.floatValue)
                    }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp),
                thickness = 5.dp,
                color = Color.Green
            )

            // Playlists
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Icon(
                        imageVector = Icons.Filled.PlaylistAddCircle,
                        contentDescription = "Save to playlist",
                        tint = Color.Green,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp))
                    Text(
                        "Save To".uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Green
                    )
                }

                // List of playlists
                LazyRow {
                    items(data) { playlist ->
                        val isSongInPlaylist = savedPlaylistItems.any {
                            it.playlistId.toInt() == playlist.playlistId && it.audioTitle == audioFile.title
                        }

                        Button(
                            colors = if (isSongInPlaylist) {
                                ButtonColors(
                                    Color.Green,
                                    contentColor = Color.DarkGray,
                                    Color.Green,
                                    Color.DarkGray
                                )
                            } else ButtonDefaults.buttonColors(),
                            onClick = {
                                if (isSongInPlaylist) {
                                    itemViewModel.deletePlaylistItem(
                                        playlist.name,
                                        audioFile.title
                                    )
                                } else {
                                    itemViewModel.addPlaylistItem(
                                        playlist.name,
                                        audioFile.title
                                    )
                                }
                            }) {
                            Text(text = playlist.name)
                        }

                    }
                }
            }

        }
    }
}

/**
 * Helper function to format time in milliseconds to minutes and seconds
 *
 * @param timeInMillis (Long) The time in milliseconds
 */
fun formatTime(timeInMillis: Long): String {
    val totalSeconds = timeInMillis / 1000
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60

    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}

/**
 * Helper function to round a float to 2 decimal places
 *
 * @param value (Float) The value to round
 */
private fun roundFloatToTwoDecimals(value: Float): Float {
    return BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP).toFloat()
}

/**
 * Helper function to add or remove a song from favourites DB table
 *
 * @param audioFile The audio file to toggle
 * @param audioFiles The mutable list of audio files. We need to update the audio file in here too
 * @param addFavourite To add a favourite in the DB. See FavouriteAudioViewModel [optional]
 * @param deleteFavourite To delete a favourite in the DB. See FavouriteAudioViewModel [optional]
 */
fun toggleFavorite(
    audioFile: AudioFile,
    audioFiles: MutableList<AudioFile>,
    addFavourite: (name: String) -> Unit?,
    deleteFavourite: (name: String) -> Unit?
) {
    val index = audioFiles.indexOf(audioFile)
    if (index != -1) {
        audioFiles[index] = audioFile.copy(isFavorite = !audioFile.isFavorite)

        if (audioFile.isFavorite) {
            deleteFavourite(audioFile.title)
        } else {
            addFavourite(audioFile.title)
        }
    }
}