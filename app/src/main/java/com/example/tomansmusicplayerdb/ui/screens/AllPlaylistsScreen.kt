package com.example.tomansmusicplayerdb.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.tomansmusicplayerdb.data.local.database.Playlist
import com.example.tomansmusicplayerdb.ui.model.PlaylistUiState
import com.example.tomansmusicplayerdb.ui.model.PlaylistViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlaylistListScreen(
    navController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    var playlistName = remember { mutableStateOf("") }

    // Edit
    var showEditDialog by remember { mutableStateOf(false) }
    var playlistToEdit by remember { mutableStateOf<Playlist?>(null) }
    var newPlaylistName by remember { mutableStateOf("") }

    // Delete
    var showDeleteDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<Playlist?>(null) }

    val savedPlaylists by viewModel.uiState.collectAsStateWithLifecycle()
    var data = remember { listOf<Playlist>() }
    if (savedPlaylists is PlaylistUiState.Success) {
        data = (savedPlaylists as PlaylistUiState.Success).data
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { showDialog.value = true }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Create Playlist")
            }
        },
        modifier = Modifier.padding(
            0.dp,
            70.dp,
            0.dp,
            innerPadding.calculateBottomPadding()
        ),
        content = {
            LazyColumn {
                items(data) { playlist ->
                    PlaylistCard(
                        playlist,
                        onEdit = {
                            playlistToEdit = playlist
                            newPlaylistName = playlist.name // Pre-fill with current name
                            showEditDialog = true
                        },
                        onDelete = {
                            playlistToDelete = playlist
                            showDeleteDialog = true
                        },
                        onClick = { navController.navigate("playlists/${it.name}") }
                    )
                }
            }
        },
    )


    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Create Playlist") },
            text = {
                TextField(
                    value = playlistName.value,
                    onValueChange = { playlistName.value = it },
                    label = { Text("Playlist Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addPlaylist(playlistName.value)
                    showDialog.value = false
                    playlistName = mutableStateOf("") // Reset input field
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Playlist") },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    playlistToEdit?.let {
                        viewModel.updatePlaylistName(it.playlistId, newPlaylistName)
                    }
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this playlist?") },
            confirmButton = {
                Button(
                    colors = ButtonColors(
                        MaterialTheme.colorScheme.error,
                        contentColor = Color.DarkGray,
                        disabledContentColor = Color.DarkGray,
                        disabledContainerColor = Color.LightGray
                    ), onClick = {
                        playlistToDelete?.let {
                            viewModel.deletePlaylist(it.playlistId)
                        }
                        showDeleteDialog = false
                    }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onClick: (Playlist) -> Unit,
    onEdit: (Playlist) -> Unit,
    onDelete: (Playlist) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(playlist) } // Click on card for navigation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = playlist.name, style = MaterialTheme.typography.headlineSmall)

            // Wrap IconButton and DropdownMenu in a Box for positioning
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More menu")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            onEdit(playlist)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDelete(playlist)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}