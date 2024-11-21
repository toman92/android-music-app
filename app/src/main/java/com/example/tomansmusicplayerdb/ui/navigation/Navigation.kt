/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tomansmusicplayerdb.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tomansmusicplayerdb.ui.model.FavouriteAudioUiState
import com.example.tomansmusicplayerdb.ui.model.FavouriteViewModel
import com.example.tomansmusicplayerdb.ui.player.AudioFile
import com.example.tomansmusicplayerdb.ui.screens.ScreenHolder
import com.example.tomansmusicplayerdb.ui.screens.MusicList
import com.example.tomansmusicplayerdb.ui.screens.PlaylistDetailsScreen
import com.example.tomansmusicplayerdb.ui.screens.PlaylistListScreen

@Composable
fun MainNavigation(
    audioFiles: MutableList<AudioFile>,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    // Get favourites and toggle favourite audio files
    val savedItems by viewModel.uiState.collectAsStateWithLifecycle()
    if (savedItems is FavouriteAudioUiState.Success) {
        val data = (savedItems as FavouriteAudioUiState.Success).data
        markFavouriteAudioFiles(audioFiles, data) // see helper function below
    }

    val favouriteAudioFiles = audioFiles.filter { it.isFavorite }.toMutableList()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = ScreenRoute.All.route,
        ) {
            // All music screen
            composable(ScreenRoute.All.route) {
                ScreenHolder(
                    totalAudioFiles = audioFiles.size,
                    totalFavouriteFiles = favouriteAudioFiles.size,
                    navController = navController
                ) {
                    MusicList(
                        audioFiles = audioFiles,
                        isPlaylist = false,
                        viewModel::addFavouriteAudio,
                        viewModel::deleteFavouriteAudio
                    )
                }
            }

            // Favourite music screen
            composable(ScreenRoute.Favorites.route) {
                ScreenHolder(
                    totalAudioFiles = favouriteAudioFiles.size,
                    totalFavouriteFiles = favouriteAudioFiles.size,
                    navController = navController
                ) {
                    MusicList(
                        audioFiles = favouriteAudioFiles,
                        isPlaylist = false,
                        viewModel::addFavouriteAudio,
                        viewModel::deleteFavouriteAudio
                    )
                }
            }

            // All playlists screen
            composable(ScreenRoute.Playlists.route) {
                ScreenHolder(
                    totalAudioFiles = audioFiles.size,
                    totalFavouriteFiles = favouriteAudioFiles.size,
                    navController = navController
                ) {
                    PlaylistListScreen(
                        navController = navController,
                        innerPadding
                    )
                }
            }

            // Playlist
            composable(ScreenRoute.PlaylistDetails.route) { backStackEntry ->
                val playlistName = backStackEntry.arguments?.getString("playlistName") ?: ""
                PlaylistDetailsScreen(playlistName, audioFiles)
            }
        }
    }
}

// Helper function to mark favourite audio files on app load
private fun markFavouriteAudioFiles(audioFiles: MutableList<AudioFile>, savedFiles: List<String>) {
    for (audioFile in audioFiles) {
        if (savedFiles.contains(audioFile.title)) {
            audioFile.isFavorite = true
        }
    }
}