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

package com.example.tomansmusicplayerdb.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomansmusicplayerdb.data.DefaultPlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.tomansmusicplayerdb.data.local.database.Playlist
import com.example.tomansmusicplayerdb.ui.model.PlaylistUiState.Success
import javax.inject.Inject

/**
 * View Model for playlist repository and the list of items.
 */
@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: DefaultPlaylistRepository
) : ViewModel() {

    val uiState: StateFlow<PlaylistUiState> = playlistRepository
        .playlists.map<List<Playlist>, PlaylistUiState>(::Success)
        .catch { emit(PlaylistUiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlaylistUiState.Loading)

    fun addPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
        }
    }

    fun updatePlaylistName(playlistId: Int, newName: String) {
        viewModelScope.launch {
            playlistRepository.updatePlaylistName(playlistId, newName)
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }
}

sealed interface PlaylistUiState {
    object Loading : PlaylistUiState
    data class Error(val throwable: Throwable) : PlaylistUiState
    data class Success(val data: List<Playlist>) : PlaylistUiState
}
