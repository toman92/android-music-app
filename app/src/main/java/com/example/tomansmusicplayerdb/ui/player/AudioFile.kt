package com.example.tomansmusicplayerdb.ui.player

/**
 * Represents data fields on each audio file.
 */
data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val data: String, // Holds path to the file,
    val duration: Long = 0L,
    var isFavorite: Boolean = false
)