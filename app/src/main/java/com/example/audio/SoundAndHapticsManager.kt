package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.domain.AudioMode
import com.example.domain.GameSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundAndHapticsManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    fun playCastSfx(settings: GameSettings) {
        if (settings.sfxVolume <= 0.02f) return
        playTone(freqStart = 320.0, freqEnd = 740.0, durationMs = 160, settings = settings)
    }

    fun playBiteAlert(settings: GameSettings) {
        if (settings.sfxVolume > 0.02f) {
            playTone(freqStart = 880.0, freqEnd = 1175.0, durationMs = 190, settings = settings)
        }
        vibratePulse(settings, durationMs = 110L, amplitude = 200)
    }

    fun playReelTick(settings: GameSettings) {
        if (settings.sfxVolume <= 0.02f) return
        playTone(freqStart = 520.0, freqEnd = 560.0, durationMs = 40, settings = settings)
    }

    fun playCatchFanfare(settings: GameSettings, isRare: Boolean) {
        if (settings.sfxVolume > 0.02f) {
            scope.launch {
                val notes = if (isRare) listOf(523.25, 659.25, 783.99, 1046.5) else listOf(523.25, 659.25, 783.99)
                notes.forEach { f ->
                    playToneSync(f, f, 95, settings)
                }
            }
        }
        vibratePulse(settings, durationMs = if (isRare) 180L else 90L, amplitude = 220)
    }

    fun playSnapAlert(settings: GameSettings) {
        if (settings.sfxVolume > 0.02f) {
            playTone(freqStart = 300.0, freqEnd = 140.0, durationMs = 220, settings = settings)
        }
        vibratePulse(settings, durationMs = 200L, amplitude = 255)
    }

    private fun vibratePulse(settings: GameSettings, durationMs: Long, amplitude: Int) {
        if (!settings.hapticFeedback) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }

    private fun playTone(freqStart: Double, freqEnd: Double, durationMs: Int, settings: GameSettings) {
        scope.launch {
            playToneSync(freqStart, freqEnd, durationMs, settings)
        }
    }

    private fun playToneSync(freqStart: Double, freqEnd: Double, durationMs: Int, settings: GameSettings) {
        try {
            val sampleRate = if (settings.audioMode == AudioMode.HIFI) 44100 else 22050
            val count = (sampleRate * durationMs / 1000).coerceAtLeast(1)
            val samples = ShortArray(count)
            val vol = (settings.sfxVolume * 0.25f).coerceIn(0f, 0.35f)
            var phase = 0.0
            for (i in 0 until count) {
                val t = i.toDouble() / count
                val freq = freqStart + (freqEnd - freqStart) * t
                val env = if (i < count / 8) i.toDouble() / (count / 8) else (1.0 - (i - count / 8).toDouble() / (count * 7 / 8)).coerceAtLeast(0.0)
                phase += 2.0 * PI * freq / sampleRate
                samples[i] = (sin(phase) * env * vol * Short.MAX_VALUE).toInt().toShort()
            }
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 20L)
            track.stop()
            track.release()
        } catch (_: Exception) {
        }
    }
}
