package com.example.tomansmusicplayerdb.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tomansmusicplayerdb.ui.player.formatTime
import com.example.tomansmusicplayerdb.ui.model.FavouriteViewModel
import com.example.tomansmusicplayerdb.ui.player.AudioFile
import com.example.tomansmusicplayerdb.ui.model.PlaylistItemViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlaylistDetailsScreen(
    playlistName: String,
    audioFiles: MutableList<AudioFile>,
    //innerPadding: PaddingValues,
    viewModel: PlaylistItemViewModel = hiltViewModel(),
    favouriteViewModel: FavouriteViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val savedPlaylistItems =
        viewModel.getPlaylistItems(playlistName).collectAsStateWithLifecycle(
            initialValue = emptyList(),
            lifecycle = lifecycleOwner.lifecycle
        ).value

    val playlistAudioFiles = audioFiles.filter { it.title in savedPlaylistItems }.toMutableList()

    val playlistDuration = playlistAudioFiles.sumOf { it.duration }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    "$playlistName (${playlistAudioFiles.size})",
                    color = Color.Green
                )
            })
        }
    ) { innerPadding ->

        Column(modifier = Modifier.padding(0.dp, innerPadding.calculateTopPadding(), 0.dp, 0.dp)) {
            Text(
                "Length: ${formatTime(playlistDuration)}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Green,
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp)
            )
            Divider(
                modifier = Modifier
                    .height(4.dp)
                    .background(Color.Green)
            )
            Spacer(modifier = Modifier.height(10.dp))
            MusicList(
                audioFiles = playlistAudioFiles,
                isPlaylist = true,
                favouriteViewModel::addFavouriteAudio,
                favouriteViewModel::deleteFavouriteAudio
            )

        }
    }
}