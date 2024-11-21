package com.example.tomansmusicplayerdb.ui.player

import android.media.audiofx.Visualizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Captures and analyzes audio waveform data using the Visualizer API.
 */
class WaveformAnalyzer {
    private var visualizer: Visualizer? = null
    private var isCapturing = AtomicBoolean(false)
    private val waveformListeners = mutableListOf<(ByteArray) -> Unit>()

    fun start(sessionId: Int) {
        if (visualizer == null) {
            visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                scalingMode = Visualizer.SCALING_MODE_NORMALIZED
                enabled = true
            }
            isCapturing.set(true)
            startCaptureLoop()
        }
    }

    fun stop() {
        isCapturing.set(false)
        visualizer?.setEnabled(false)
        visualizer?.release()
        visualizer = null
    }

    fun addWaveformListener(listener: (ByteArray) -> Unit) {
        waveformListeners.add(listener)
    }

    fun removeWaveformListener(listener: (ByteArray) -> Unit) {
        waveformListeners.remove(listener)
    }

    private fun startCaptureLoop() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isCapturing.get() && visualizer?.enabled == true) {
                visualizer?.let {
                    val captureSize = it.captureSize
                    val waveformBytes = ByteArray(captureSize)
                    it.getWaveForm(waveformBytes)
                    // Notify listeners with new waveform data
                    waveformListeners.forEach { listener ->
                        listener(waveformBytes)
                    }
                    delay(16)
                }
            }
        }
    }
}