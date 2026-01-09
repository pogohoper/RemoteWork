package com.wineglassvibrator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var audioProcessor: AudioProcessor
    private lateinit var frequencyValue: TextView
    private lateinit var statusText: TextView
    private lateinit var listenButton: MaterialButton
    private lateinit var playButton: MaterialButton

    private var detectedFrequency = 0f

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupAudioProcessor()
        checkPermissions()
    }

    private fun initializeViews() {
        frequencyValue = findViewById(R.id.frequencyValue)
        statusText = findViewById(R.id.statusText)
        listenButton = findViewById(R.id.listenButton)
        playButton = findViewById(R.id.playButton)

        listenButton.setOnClickListener {
            if (audioProcessor.isListening) {
                stopListening()
            } else {
                startListening()
            }
        }

        playButton.setOnClickListener {
            if (audioProcessor.isPlaying) {
                stopPlayback()
            } else {
                startPlayback()
            }
        }
    }

    private fun setupAudioProcessor() {
        audioProcessor = AudioProcessor()
        audioProcessor.onFrequencyDetected = { frequency ->
            runOnUiThread {
                detectedFrequency = frequency
                updateFrequencyDisplay(frequency)
                playButton.isEnabled = true
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Microphone permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Microphone permission is required to detect frequencies",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startListening() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show()
            checkPermissions()
            return
        }

        audioProcessor.startListening()
        listenButton.text = "Stop Listening"
        listenButton.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
        statusText.text = "Listening..."
        statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
    }

    private fun stopListening() {
        audioProcessor.stopListening()
        listenButton.text = "Start Listening"
        listenButton.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_blue_dark)
        statusText.text = "Stopped"
        statusText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
    }

    private fun startPlayback() {
        if (detectedFrequency <= 0) {
            Toast.makeText(this, "No frequency detected yet", Toast.LENGTH_SHORT).show()
            return
        }

        audioProcessor.startPlayback(detectedFrequency)
        playButton.text = "Stop Playing"
        playButton.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark)
        statusText.text = "Playing ${String.format("%.1f", detectedFrequency)} Hz"
    }

    private fun stopPlayback() {
        audioProcessor.stopPlayback()
        playButton.text = "Play Frequency"
        playButton.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
        statusText.text = "Ready"
    }

    private fun updateFrequencyDisplay(frequency: Float) {
        frequencyValue.text = "${String.format("%.1f", frequency)} Hz"
    }

    override fun onDestroy() {
        super.onDestroy()
        audioProcessor.release()
    }

    override fun onPause() {
        super.onPause()
        if (audioProcessor.isPlaying) {
            stopPlayback()
        }
    }
}
