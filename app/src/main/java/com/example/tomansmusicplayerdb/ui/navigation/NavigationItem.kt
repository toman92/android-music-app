package com.example.tomansmusicplayerdb.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class to hold navigation item information.
 * title - Eg. All, Favourites, Playlist
 * route - navigation route
 * icon to display on bottom navigation
 */
data class NavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)