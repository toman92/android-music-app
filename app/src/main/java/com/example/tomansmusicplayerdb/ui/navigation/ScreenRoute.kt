package com.example.tomansmusicplayerdb.ui.navigation

/**
 * Sealed class to hold Screen routes screen.
 * Sealed classes are like enums but with a bit more flexibility
 * Great for a single place to store constants such as routes
 * See MainScreen or Navigation for examples of usage
 */
sealed class ScreenRoute(val route: String) {
    data object All : ScreenRoute("main")
    data object Favorites : ScreenRoute("favorites")
    data object Playlists : ScreenRoute("playlists")
    data object PlaylistDetails : ScreenRoute("playlists/{playlistName}")
}