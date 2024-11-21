package com.example.tomansmusicplayerdb.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.example.tomansmusicplayerdb.ui.navigation.ScreenRoute

/**
 * Wrapper component for all screens
 * Has name of screen Eg. All, Favourites, playlists
 * and how many items are in the list
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHolder(
    totalAudioFiles: Int,
    totalFavouriteFiles: Int,
    navController: NavHostController,
    content: @Composable () -> Unit
) {

    Scaffold(
        topBar = {
            val size = remember { mutableIntStateOf(0) }
            if (navController.currentBackStackEntry?.destination?.route == ScreenRoute.All.route) {
                size.intValue = totalAudioFiles
            } else if (navController.currentBackStackEntry?.destination?.route == ScreenRoute.Favorites.route) {
                size.intValue = totalFavouriteFiles
            }
            TopAppBar(title = {
                Text(
                    "Music Player ${
                        if (navController.currentBackStackEntry?.destination?.route?.contains(
                                ScreenRoute.Playlists.route
                            ) == false
                        ) "(${size.intValue} files)" else ""
                    }"
                )
            })
        },
    ) {
        content()
    }
}