package com.example.tomansmusicplayerdb.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tomansmusicplayerdb.data.local.database.FavouriteAudio
import com.example.tomansmusicplayerdb.data.local.database.FavouriteAudioDao

@Database(entities = [FavouriteAudio::class, Playlist::class, PlaylistItem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteAudioDao(): FavouriteAudioDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistItemDao(): PlaylistItemDao
}