package com.example.tomansmusicplayerdb.ui.navigation

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

/**
 * Bottom navigation bar component.
 */
@Composable
fun BottomNavigationBar(navController: NavController) {

    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        NavigationItem("All", ScreenRoute.All.route, Icons.Default.Home),
        NavigationItem("Favorites", ScreenRoute.Favorites.route, Icons.Default.Star),
        NavigationItem(
            "Playlists",
            ScreenRoute.Playlists.route,
            Icons.AutoMirrored.Default.FeaturedPlayList
        )
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selectedItem == index) Color.Green else MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selectedItem == index) Color.Green else MaterialTheme.colorScheme.onSurface
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.id) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}