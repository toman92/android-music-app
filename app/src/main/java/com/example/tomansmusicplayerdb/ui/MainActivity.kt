/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tomansmusicplayerdb.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.tomansmusicplayerdb.ui.player.AudioFile
import com.example.tomansmusicplayerdb.ui.navigation.MainNavigation
import dagger.hilt.android.AndroidEntryPoint
import com.example.tomansmusicplayerdb.ui.theme.MyApplicationTheme

private const val REQUEST_READ_STORAGE_PERMISSION = 100000;
private const val REQUEST_RECORD_AUDIO_PERMISSION = 100001;

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var audioFiles: MutableList<AudioFile>

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioFiles = mutableListOf()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainNavigation(audioFiles)
                }
            }
        }

        // New way to request permissions - has support to reload data after permission change
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Reload audio files
                    loadAudioFiles()
                } else {
                    // TODO - Permission denied show alert
                }
            }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            loadAudioFiles() // Load audio files if permission is already granted
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun loadAudioFiles() {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
        )

        // Define the MIME types
        val selection =
            MediaStore.Audio.Media.MIME_TYPE +
                    " IN ('audio/mpeg', 'audio/wav', 'audio/x-wav, 'audio/mp4', 'audio/x-ms-wma', 'audio/ogg', 'audio/aac', 'audio/dsd', 'audio/amr')"

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            //selection,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                //val track = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
                //val trackNum = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.CD_TRACK_NUMBER))
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val album =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                //val albumArtist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST))
                val data =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                val duration =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

                audioFiles.add(AudioFile(id, title, artist, album, data, duration))
            } while (cursor.moveToNext())
            cursor.close()
        }
    }
}
