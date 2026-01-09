package com.wineglassvibrator

import kotlin.math.*

class FFTAnalyzer(private val sampleRate: Int) {

    fun findDominantFrequency(audioData: ShortArray): Float {
        val fftSize = nearestPowerOf2(audioData.size)
        val complex = DoubleArray(fftSize * 2)

        // Convert audio data to complex numbers and apply Hamming window
        for (i in 0 until min(audioData.size, fftSize)) {
            val window = 0.54 - 0.46 * cos(2.0 * PI * i / audioData.size)
            complex[i * 2] = audioData[i].toDouble() * window
            complex[i * 2 + 1] = 0.0
        }

        // Perform FFT
        fft(complex, fftSize, false)

        // Calculate magnitude spectrum
        val magnitudes = FloatArray(fftSize / 2)
        for (i in 0 until fftSize / 2) {
            val real = complex[i * 2]
            val imag = complex[i * 2 + 1]
            magnitudes[i] = sqrt(real * real + imag * imag).toFloat()
        }

        // Find peak frequency (ignore DC component and very low frequencies)
        var maxMagnitude = 0f
        var maxIndex = 0
        val minIndex = (50.0 * fftSize / sampleRate).toInt() // Start from 50 Hz

        for (i in minIndex until magnitudes.size) {
            if (magnitudes[i] > maxMagnitude) {
                maxMagnitude = magnitudes[i]
                maxIndex = i
            }
        }

        // Convert bin index to frequency
        val frequency = maxIndex.toFloat() * sampleRate / fftSize

        // Only return if magnitude is significant
        return if (maxMagnitude > 100) frequency else 0f
    }

    private fun nearestPowerOf2(n: Int): Int {
        var power = 1
        while (power < n) power = power shl 1
        return power
    }

    private fun fft(x: DoubleArray, n: Int, inverse: Boolean) {
        // Bit-reverse
        var j = 0
        for (i in 0 until n - 1) {
            if (i < j) {
                var temp = x[i * 2]
                x[i * 2] = x[j * 2]
                x[j * 2] = temp

                temp = x[i * 2 + 1]
                x[i * 2 + 1] = x[j * 2 + 1]
                x[j * 2 + 1] = temp
            }
            var k = n / 2
            while (k <= j) {
                j -= k
                k /= 2
            }
            j += k
        }

        // Compute FFT
        val direction = if (inverse) 1 else -1
        var length = 2
        while (length <= n) {
            val angle = 2.0 * PI / length * direction
            val wReal = cos(angle)
            val wImag = sin(angle)

            var i = 0
            while (i < n) {
                var wnReal = 1.0
                var wnImag = 0.0

                for (j in 0 until length / 2) {
                    val idx1 = (i + j) * 2
                    val idx2 = (i + j + length / 2) * 2

                    val tempReal = wnReal * x[idx2] - wnImag * x[idx2 + 1]
                    val tempImag = wnReal * x[idx2 + 1] + wnImag * x[idx2]

                    x[idx2] = x[idx1] - tempReal
                    x[idx2 + 1] = x[idx1 + 1] - tempImag
                    x[idx1] += tempReal
                    x[idx1 + 1] += tempImag

                    val tempWn = wnReal
                    wnReal = wnReal * wReal - wnImag * wImag
                    wnImag = tempWn * wImag + wnImag * wReal
                }
                i += length
            }
            length *= 2
        }

        if (inverse) {
            for (i in 0 until n * 2) {
                x[i] /= n
            }
        }
    }
}
