package com.example.tomansmusicplayerdb.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Playlist Entity
@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Int = 0,
    val name: String
)

@Dao
interface PlaylistDao {
    // ... (Insert, get all, get by ID, delete methods)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlist: Playlist): Long // Return the ID of the inserted playlist

    @Query("SELECT * FROM playlist")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM playlist WHERE name = :name")
    fun getPlaylistByName(name: String): Flow<Playlist>

    @Query("DELETE FROM playlist WHERE playlistId = :playlistId")
    suspend fun delete(playlistId: Int)

    @Query("UPDATE playlist SET name = :newName WHERE playlistId = :playlistId")
    suspend fun updatePlaylistName(playlistId: Int, newName: String)
}

