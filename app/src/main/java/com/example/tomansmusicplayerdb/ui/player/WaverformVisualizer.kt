package com.example.tomansmusicplayerdb.ui.player

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi

/**
 * Component to display waveform data from waveform analyzer
 */
@OptIn(UnstableApi::class)
@Composable
fun WaveformVisualizer(sessionId: Int?, exoPlayerWrapper: ExoPlayerWrapper) {
    val waveform: MutableState<ByteArray?> = remember { mutableStateOf(null) }
    val waveformAnalyzer = remember { WaveformAnalyzer() }

    //Log.d("WaveformVisualizer", "ExoPlayer audioSessionId: $sessionId")

    // Start or stop the analyzer based on sessionId
    LaunchedEffect(key1 = sessionId) {
        if (sessionId != null && exoPlayerWrapper.getIsPlaying()) {
            waveformAnalyzer.start(sessionId)
        } else {
            waveformAnalyzer.stop()
        }
    }

    // Listen for waveform updates
    LaunchedEffect(key1 = waveformAnalyzer) {
        waveformAnalyzer.addWaveformListener { bytes ->
            waveform.value = bytes
        }
    }

    // Dispose the listener when it moves out of view
    // TODO - update so it disposes on session ID.
    //        This is to stop the visualiser stopping when scrolling back up to a playing song
    DisposableEffect(Unit) {
        onDispose {
            waveformAnalyzer.removeWaveformListener { bytes ->
                waveform.value = bytes
            }
            waveformAnalyzer.stop()
        }
    }
    Canvas(
        modifier = Modifier
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
            .fillMaxWidth()
            .height(60.dp)
    ) {
        waveform.value?.let { bytes ->
            val width = size.width
            val height = size.height
            val numBars = bytes.size // Set the number of bars displayed

            val barWidth = width / numBars // Set width of each bar

            val minBarHeight = 2.dp.toPx() // Set a minimum bar height
            val maxBarHeight = height // Maximum height remains the same

            // Downsample and smooth the waveform data - See helper function below
            val downsampledData = downsampleAndSmooth(bytes, numBars)

            for (i in downsampledData.indices) {
                val amplitude = downsampledData[i]
                val barHeight =
                    minBarHeight + (amplitude / 255f) * (maxBarHeight - minBarHeight)

                // Calculate color shade based on amplitude
                val color = Color.Green.copy(alpha = amplitude / 255f)

                val path = Path()
                val topLeft = Offset(i * barWidth, height / 2 - barHeight / 2)
                val topRight = Offset((i + 1) * barWidth, height / 2 - barHeight / 2)
                val bottomRight = Offset((i + 1) * barWidth, height / 2 + barHeight / 2)
                val bottomLeft = Offset(i * barWidth, height / 2 + barHeight / 2)

                path.moveTo(topLeft.x, topLeft.y)

                path.quadraticBezierTo(
                    (topLeft.x + topRight.x) / 2,
                    topLeft.y - barHeight / 6, // Control point for top curve
                    topRight.x,
                    topRight.y
                )

                path.lineTo(bottomRight.x, bottomRight.y)
                path.quadraticBezierTo(
                    (bottomLeft.x + bottomRight.x) / 2,
                    bottomLeft.y + barHeight / 6, // Control point for bottom curve
                    bottomLeft.x,
                    bottomLeft.y
                )
                path.close()

                drawPath(
                    path = path,
                    color = color
                )
            }
        }
    }
}

// Helper function to downsample and smooth the waveform data
private fun downsampleAndSmooth(bytes: ByteArray, numBars: Int): List<Float> {
    val result = mutableListOf<Float>()
    val interval = bytes.size / numBars

    for (i in 0 until numBars) {
        val startIndex = i * interval
        val endIndex = minOf((i + 1) * interval, bytes.size)
        var sum = 0f

        for (j in startIndex until endIndex) {
            sum += bytes[j].toInt() and 0xFF // Normalize to 0-255
        }

        val average = sum / (endIndex - startIndex)
        result.add(average)
    }

    return result
}