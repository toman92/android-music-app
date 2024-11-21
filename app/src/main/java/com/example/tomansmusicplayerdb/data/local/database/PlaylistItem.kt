package com.example.tomansmusicplayerdb.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// PlaylistItem Entity
@Entity(
    tableName = "playlist_item",
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"]
        ),
    ]
)
data class PlaylistItem(
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    val playlistId: Long,
    val audioTitle: String
)

// PlaylistItemDao
@Dao
interface PlaylistItemDao {
    // ... (Add, remove, get audio files for playlist methods)
    @Query("INSERT INTO playlist_item (playlistId, audioTitle) VALUES ((SELECT playlistId from playlist WHERE name = :playlistName), :audioTitle)")
    suspend fun insert(
        playlistName: String,
        audioTitle: String
    ): Long // Return the ID of the inserted item

    @Query("SELECT * FROM playlist_item WHERE playlistId = (SELECT playlistId FROM playlist WHERE name = :playlistName)")
    fun getAudioFilesForPlaylist(playlistName: String): Flow<List<PlaylistItem>>

    @Query("SELECT * FROM playlist_item")
    fun getAll(): Flow<List<PlaylistItem>>

    @Query("DELETE FROM playlist_item where audioTitle = :audioTitle AND playlistId = (SELECT playlistId FROM playlist WHERE name = :playlistName)")
    suspend fun deleteAudioFileFromPlaylist(playlistName: String, audioTitle: String)

}