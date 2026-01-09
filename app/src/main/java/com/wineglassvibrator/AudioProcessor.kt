package com.wineglassvibrator

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import kotlinx.coroutines.*
import kotlin.math.*

class AudioProcessor {

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var recordingJob: Job? = null
    private var playbackJob: Job? = null

    private val fftAnalyzer = FFTAnalyzer(sampleRate)
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    var onFrequencyDetected: ((Float) -> Unit)? = null
    var isListening = false
        private set
    var isPlaying = false
        private set

    private var currentFrequency = 0f

    fun startListening() {
        if (isListening) return

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize * 2
            )

            audioRecord?.startRecording()
            isListening = true

            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ShortArray(bufferSize)

                while (isActive && isListening) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0

                    if (read > 0) {
                        val frequency = fftAnalyzer.findDominantFrequency(buffer)

                        if (frequency > 0) {
                            currentFrequency = frequency
                            withContext(Dispatchers.Main) {
                                onFrequencyDetected?.invoke(frequency)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopListening()
        }
    }

    fun stopListening() {
        isListening = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun startPlayback(frequency: Float) {
        if (isPlaying) {
            stopPlayback()
        }

        val playbackFrequency = if (frequency > 0) frequency else currentFrequency
        if (playbackFrequency <= 0) return

        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                audioFormat
            )

            audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                audioFormat,
                minBufferSize,
                AudioTrack.MODE_STREAM
            )

            audioTrack?.play()
            isPlaying = true

            playbackJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ShortArray(minBufferSize / 2)
                val amplitude = Short.MAX_VALUE * 0.8
                var phase = 0.0
                val phaseIncrement = 2.0 * PI * playbackFrequency / sampleRate

                while (isActive && isPlaying) {
                    for (i in buffer.indices) {
                        buffer[i] = (amplitude * sin(phase)).toInt().toShort()
                        phase += phaseIncrement
                        if (phase > 2.0 * PI) {
                            phase -= 2.0 * PI
                        }
                    }

                    audioTrack?.write(buffer, 0, buffer.size)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlayback()
        }
    }

    fun stopPlayback() {
        isPlaying = false
        playbackJob?.cancel()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    fun release() {
        stopListening()
        stopPlayback()
    }
}
