package com.example.tomansmusicplayerdb.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomansmusicplayerdb.data.DefaultPlaylistRepository
import com.example.tomansmusicplayerdb.data.local.database.PlaylistItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View Model for playlist items.
 */
@HiltViewModel
class PlaylistItemViewModel @Inject constructor(
    private val playlistRepository: DefaultPlaylistRepository,
) : ViewModel() {
    fun getPlaylistItems(playlistName: String): Flow<List<String>> {
        return playlistRepository.getPlaylistItems(playlistName)
            .map { items -> items.map { it.audioTitle } }
    }

    fun addPlaylistItem(playlistName: String, audioTitle: String) {
        viewModelScope.launch {
            playlistRepository.addAudioToPlaylist(playlistName, audioTitle)
        }
    }

    fun getAllPlaylistItems(): Flow<List<PlaylistItem>> {
        return playlistRepository.getAllPlaylistItems()
    }

    fun deletePlaylistItem(playlistName: String, audioTitle: String) {
        viewModelScope.launch {
            playlistRepository.removeAudioFromPlaylist(playlistName, audioTitle)
        }
    }
}