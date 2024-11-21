package com.example.tomansmusicplayerdb.data

import com.example.tomansmusicplayerdb.data.local.database.Playlist
import com.example.tomansmusicplayerdb.data.local.database.PlaylistDao
import com.example.tomansmusicplayerdb.data.local.database.PlaylistItem
import com.example.tomansmusicplayerdb.data.local.database.PlaylistItemDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlaylistRepository {
    val playlists: Flow<List<Playlist>>

    suspend fun createPlaylist(name: String)
}

class DefaultPlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistItemDao: PlaylistItemDao,
) : PlaylistRepository {

    override val playlists = playlistDao.getAllPlaylists()

    // ... (Methods for playlist operations)
    override suspend fun createPlaylist(name: String) {
        playlistDao.insert(Playlist(name = name)) // Assuming insert returns the ID
    }

    suspend fun updatePlaylistName(playlistId: Int, newName: String) {
        playlistDao.updatePlaylistName(playlistId, newName)
    }

    suspend fun deletePlaylist(playlistId: Int) {
        playlistDao.delete(playlistId)
    }

    fun getPlaylistByName(name: String): Flow<Playlist> {
        return playlistDao.getPlaylistByName(name)
    }

    fun getPlaylistItems(playlistName: String): Flow<List<PlaylistItem>> {
        return playlistItemDao.getAudioFilesForPlaylist(playlistName)
    }

    fun getAllPlaylistItems(): Flow<List<PlaylistItem>> {
        return playlistItemDao.getAll()
    }

    suspend fun addAudioToPlaylist(playlistName: String, audioTitle: String) {
        playlistItemDao.insert(playlistName = playlistName, audioTitle = audioTitle)
    }

    suspend fun removeAudioFromPlaylist(playlistName: String, audioTitle: String) {
        playlistItemDao.deleteAudioFileFromPlaylist(
            playlistName = playlistName,
            audioTitle = audioTitle
        )
    }

}
