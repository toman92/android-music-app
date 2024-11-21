package com.example.tomansmusicplayerdb.ui.player

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
//import androidx.media3.common.AudioAttributes
import android.media.AudioAttributes
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

/**
 * Wrapper class for ExoPlayer to control playback.
 */
class ExoPlayerWrapper(audioFiles: MutableList<AudioFile>, private val context: Context) {
    private var exoPlayer: ExoPlayer? = null
    private var currentPlayingFile: AudioFile? = null
    private var isPlaying = false
    private var autoPlay = false
    private var repeat = false

    private var duration: Long = 1L

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Resume or start playback
                exoPlayer?.volume = 1f
                exoPlayer?.playWhenReady = true
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                // Stop playback and release resources
                exoPlayer?.pause()
                isPlaying = false
                exoPlayer?.playWhenReady = false
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Pause playback
                exoPlayer?.pause()
                isPlaying = false
                exoPlayer?.playWhenReady = false
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower the volume
                exoPlayer?.volume = 0.5f
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .setOnAudioFocusChangeListener(audioFocusChangeListener)
        .build()

    private var audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestAudioFocus(): Boolean {
        val focusRequestResult = audioManager.requestAudioFocus(audioFocusRequest)

        return focusRequestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun abandonAudioFocus() {
        // ... (abandon audio focus logic)
        audioManager.abandonAudioFocusRequest(audioFocusRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @androidx.annotation.OptIn(UnstableApi::class)
    fun updatePlayingState(
        audioFiles: List<AudioFile>,
        audioFile: AudioFile,
        //currentPlayingFile: MutableState<AudioFile?>,
        //isPlaying: MutableState<Boolean>,
        //autoPlay: MutableState<Boolean>,
        //duration: MutableState<Long>,
        //context: Context
    ) {
        if (audioFile != currentPlayingFile) {
            Log.i("Audio File", "Same as current file")
            exoPlayer?.release() // Release previous player if exists

            // Create new player, load audio and prepare
            val newPlayer = ExoPlayer.Builder(context).build()
            val mediaSource = MediaItem.fromUri(audioFile.data)
            newPlayer.setMediaItem(mediaSource)
            newPlayer.prepare()

            newPlayer.addListener(object : Player.Listener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayWhenReadyChanged(playWhenReady, playbackState)
                    if (playbackState == Player.STATE_READY) {
                        requestAudioFocus()
                        isPlaying = true
                        Log.i("MediaPlayer", "Started with audioFile ${audioFile.data}")
                        duration = newPlayer.duration // Update duration when available
                        //newPlayer.playWhenReady = isPlaying
                    } else if (playbackState == Player.STATE_ENDED) {
                        // Handle playback end
                        isPlaying = false
                        if (repeat) {
                            newPlayer.seekTo(0)
                            updatePlayingState(audioFiles, audioFile)
                        } else if (autoPlay) {
                            val nextIndex = audioFiles.indexOf(audioFile) + 1
                            if (nextIndex < audioFiles.size) {
                                updatePlayingState(
                                    audioFiles,
                                    audioFiles[nextIndex],
                                )
                            }
                        } else {
                            abandonAudioFocus() // Abandon focus when playback ends
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Log.e("ExoPlayer Error", "Error: ${error.message}")
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    super.onTimelineChanged(timeline, reason)
                    if (newPlayer.duration > 0) {
                        duration =
                            newPlayer.duration // Update nduration when available
                    }
                }
            })
            newPlayer.play()
            exoPlayer = newPlayer
            currentPlayingFile = audioFile
        } else {
            //Log.i("Audio File", "Not same as current file")
            exoPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    abandonAudioFocus()
                    isPlaying = false // Update isPlaying state for UI
                    //Log.i("Audio", "playing: ${isPlaying}")
                } else {
                    requestAudioFocus()
                    it.prepare()
                    it.play()
                    isPlaying = true // Update isPlaying state for UI
                    //Log.i("Audio", "playing: ${isPlaying}")
                }
            }
        }
    }

    fun stop(audioFile: AudioFile) {
        if (audioFile == currentPlayingFile) {
            exoPlayer?.let {
                it.stop()
                //it.prepare()
                abandonAudioFocus()
                it.seekTo(0)
                isPlaying = false
                //currentPlayingFile = null
            }
        }
    }

    fun getExoPlayer(): ExoPlayer? {
        return exoPlayer
    }

    fun getCurrentPlayingFile(): AudioFile? {
        return currentPlayingFile
    }

    fun getIsPlaying(): Boolean {
        return isPlaying
    }

    fun setAutoPlay(enabled: Boolean) {
        autoPlay = enabled
    }

    fun setSpeed(speed: Float) {
        exoPlayer?.playbackParameters = PlaybackParameters(speed)
    }

    fun getSpeed(): Float {
        return exoPlayer?.playbackParameters?.speed ?: 1.0f
    }

    fun setRepeat(enabled: Boolean) {
        repeat = enabled
    }
}