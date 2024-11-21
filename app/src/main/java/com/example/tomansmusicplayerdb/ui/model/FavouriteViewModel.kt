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

package com.example.tomansmusicplayerdb.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.tomansmusicplayerdb.data.FavouriteAudioRepository
import com.example.tomansmusicplayerdb.ui.model.FavouriteAudioUiState.Error
import com.example.tomansmusicplayerdb.ui.model.FavouriteAudioUiState.Loading
import com.example.tomansmusicplayerdb.ui.model.FavouriteAudioUiState.Success
import javax.inject.Inject

/**
 * View Model for favourite audio repository and the list of items.
 */
@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val favouriteAudioRepository: FavouriteAudioRepository
) : ViewModel() {

    val uiState: StateFlow<FavouriteAudioUiState> = favouriteAudioRepository
        .favouriteAudios.map<List<String>, FavouriteAudioUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addFavouriteAudio(name: String) {
        viewModelScope.launch {
            favouriteAudioRepository.add(name)
        }
    }

    fun deleteFavouriteAudio(name: String) {
        viewModelScope.launch {
            favouriteAudioRepository.delete(name)
        }
    }
}

sealed interface FavouriteAudioUiState {
    data object Loading : FavouriteAudioUiState
    data class Error(val throwable: Throwable) : FavouriteAudioUiState
    data class Success(val data: List<String>) : FavouriteAudioUiState
}
