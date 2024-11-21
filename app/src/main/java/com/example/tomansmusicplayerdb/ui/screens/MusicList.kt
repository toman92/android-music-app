package com.example.tomansmusicplayerdb.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tomansmusicplayerdb.ui.player.AudioFile
import com.example.tomansmusicplayerdb.ui.player.AudioItemCard
import com.example.tomansmusicplayerdb.ui.player.SearchAndAutoPlay
import com.example.tomansmusicplayerdb.ui.player.ExoPlayerWrapper
import com.example.tomansmusicplayerdb.ui.theme.MyApplicationTheme

/**
 * Composable function to display a list of audio files.
 *
 * @param audioFiles List of AudioFile objects to be displayed.
 * @param isPlaylist Indicating whether it's a playlist or not.
 * @param addFavourite To add an audio file to the favourites list/DB [optional]].
 * @param deleteFavourite To delete an audio file from the favourites list/DB [optional]].
 */
@Composable
fun MusicList(
    audioFiles: MutableList<AudioFile>,
    isPlaylist: Boolean = false,
    addFavourite: ((name: String) -> Unit)? = null,
    deleteFavourite: ((name: String) -> Unit)? = null
) {
    val searchQuery = remember { mutableStateOf("") }
    val filteredAudioFiles = audioFiles.filter {
        it.title.contains(searchQuery.value, ignoreCase = true) ||
                it.artist.contains(searchQuery.value, ignoreCase = true) ||
                it.album.contains(searchQuery.value, ignoreCase = true)
    }

    val context = LocalContext.current
    val exoPlayerWrapper = ExoPlayerWrapper(filteredAudioFiles.toMutableList(), context)

    MyApplicationTheme {
        Column(
            modifier = if (isPlaylist) Modifier.padding(
                0.dp,
                0.dp,
                0.dp,
                0.dp
            ) else Modifier.padding(0.dp, 70.dp, 0.dp, 0.dp)
        ) {
            SearchAndAutoPlay(searchQuery = searchQuery, exoPlayerWrapper = exoPlayerWrapper)
            LazyColumn {
                items(filteredAudioFiles) { audioFile ->
                    AudioItemCard(
                        audioFiles,
                        audioFile = audioFile,
                        exoPlayerWrapper,
                        addFavourite,
                        deleteFavourite
                    ) {
                        exoPlayerWrapper.updatePlayingState(filteredAudioFiles, audioFile)
                    }
                }
            }
        }
    }
}