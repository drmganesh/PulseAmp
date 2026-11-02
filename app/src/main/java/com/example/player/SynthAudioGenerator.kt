package com.example.player

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

object SynthAudioGenerator {

    /**
     * Generates a high quality stereo WAV file with harmonic music synthesis
     * for offline audiophile demonstration.
     */
    fun ensureDemoTracksExist(context: Context): Map<String, String> {
        val cacheDir = File(context.filesDir, "audio_tracks")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        val tracks = mapOf(
            "neon_drift" to "Neon Drift.wav",
            "eclipse_resonance" to "Eclipse Resonance.wav",
            "subfractal_void" to "Subfractal Void.wav",
            "midnight_horizon" to "Midnight Horizon.wav",
            "solaris_ambient" to "Solaris Ambient.wav"
        )

        val resultPaths = mutableMapOf<String, String>()

        for ((key, filename) in tracks) {
            val file = File(cacheDir, filename)
            if (!file.exists() || file.length() < 1000) {
                generateTrackFile(file, key)
            }
            resultPaths[key] = file.absolutePath
        }

        return resultPaths
    }

    private fun generateTrackFile(file: File, type: String) {
        val sampleRate = 44100
        val durationSeconds = 90 // 1.5 minutes of looping rich music
        val numSamples = sampleRate * durationSeconds
        val numChannels = 2
        val bytesPerSample = 2 // 16-bit PCM

        val dataSize = numSamples * numChannels * bytesPerSample
        val totalSize = 36 + dataSize

        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.put("RIFF".toByteArray())
        header.putInt(totalSize)
        header.put("WAVE".toByteArray())
        header.put("fmt ".toByteArray())
        header.putInt(16) // Subchunk1Size (16 for PCM)
        header.putShort(1.toShort()) // AudioFormat (1 for PCM)
        header.putShort(numChannels.toShort())
        header.putInt(sampleRate)
        header.putInt(sampleRate * numChannels * bytesPerSample) // ByteRate
        header.putShort((numChannels * bytesPerSample).toShort()) // BlockAlign
        header.putShort(16.toShort()) // BitsPerSample
        header.put("data".toByteArray())
        header.putInt(dataSize)

        FileOutputStream(file).use { fos ->
            fos.write(header.array())

            // Stream samples in chunks of 4096 to prevent memory spikes
            val chunkSize = 4096
            val buffer = ByteBuffer.allocate(chunkSize * numChannels * bytesPerSample).order(ByteOrder.LITTLE_ENDIAN)
            var sampleIdx = 0

            val bpm = when (type) {
                "neon_drift" -> 120
                "subfractal_void" -> 130
                "eclipse_resonance" -> 85
                "midnight_horizon" -> 95
                else -> 70
            }
            val beatDurationSec = 60.0 / bpm

            while (sampleIdx < numSamples) {
                buffer.clear()
                val chunkEnd = minOf(numSamples, sampleIdx + chunkSize)
                for (i in sampleIdx until chunkEnd) {
                    val time = i.toDouble() / sampleRate
                    val beat = (time / beatDurationSec)
                    val beatFrac = beat - beat.toInt()

                    val (left, right) = when (type) {
                        "neon_drift" -> synthSynthwave(time, beat, beatFrac)
                        "subfractal_void" -> synthCyberBass(time, beat, beatFrac)
                        "eclipse_resonance" -> synthOrchestral(time, beat)
                        "midnight_horizon" -> synthJazzRhodes(time, beat, beatFrac)
                        else -> synthAmbientFlow(time, beat)
                    }

                    // Soft clip limiter to prevent harsh digital clipping
                    val leftClamped = (tanh(left * 0.7) * 32767).toInt().coerceIn(-32767, 32767)
                    val rightClamped = (tanh(right * 0.7) * 32767).toInt().coerceIn(-32767, 32767)

                    buffer.putShort(leftClamped.toShort())
                    buffer.putShort(rightClamped.toShort())
                }
                fos.write(buffer.array(), 0, (chunkEnd - sampleIdx) * numChannels * bytesPerSample)
                sampleIdx = chunkEnd
            }
        }
    }

    private fun tanh(x: Double): Double {
        val ex = exp(2.0 * x)
        return (ex - 1.0) / (ex + 1.0)
    }

    private fun synthSynthwave(time: Double, beat: Double, beatFrac: Double): Pair<Double, Double> {
        // Bassline (Classic 80s 16th note arpeggio: A1, C2, D2, F2)
        val chordIdx = (beat / 16).toInt() % 4
        val rootFreqs = doubleArrayOf(110.0, 130.81, 146.83, 87.31)
        val root = rootFreqs[chordIdx]
        val step = (beat * 4).toInt() % 16
        val noteMult = when (step % 4) {
            0 -> 1.0
            1 -> 2.0
            2 -> 1.5
            else -> 2.0
        }
        val bassFreq = (root / 2) * noteMult
        val bassEnv = exp(-4.0 * ((beat * 4) - (beat * 4).toInt()))
        val bass = (sin(2.0 * PI * bassFreq * time) + 0.4 * sin(4.0 * PI * bassFreq * time)) * bassEnv

        // Kick drum on beats 0, 1, 2, 3
        val kickEnv = exp(-12.0 * beatFrac)
        val kickFreq = 120.0 * exp(-15.0 * beatFrac) + 45.0
        val kick = sin(2.0 * PI * kickFreq * time) * kickEnv * 1.2

        // Snare / clap on beats 1 and 3 (2nd and 4th beats)
        val isSnareBeat = (beat.toInt() % 2) == 1
        val snareEnv = if (isSnareBeat) exp(-10.0 * beatFrac) else 0.0
        val noise = (sin(1542.0 * time) * 321.0 - (sin(1542.0 * time) * 321.0).toInt()).toDouble()
        val snare = (noise * 0.6 + sin(2.0 * PI * 220.0 * time) * 0.4) * snareEnv

        // Synth pad chords
        val pad = 0.3 * (sin(2.0 * PI * root * time) + sin(2.0 * PI * (root * 1.5) * time) + sin(2.0 * PI * (root * 1.25) * time))

        val left = kick * 0.9 + snare * 0.7 + bass * 0.8 + pad * 0.5
        val right = kick * 0.9 + snare * 0.7 + bass * 0.7 + pad * 0.6
        return Pair(left, right)
    }

    private fun synthCyberBass(time: Double, beat: Double, beatFrac: Double): Pair<Double, Double> {
        // Deep sub bass at 40-55 Hz with heavy harmonic distortion
        val subFreq = 50.0 + 10.0 * sin(0.5 * time)
        val sub = sin(2.0 * PI * subFreq * time) * 1.1 + 0.3 * sin(4.0 * PI * subFreq * time)

        // Punchy sub kick
        val kickEnv = exp(-15.0 * beatFrac)
        val kick = sin(2.0 * PI * (140.0 * exp(-18.0 * beatFrac) + 40.0) * time) * kickEnv

        // Cyber plucks with stereo ping-pong delay
        val pluckFreq = 440.0 * (1.0 + ((beat * 2).toInt() % 8) * 0.25)
        val pluckEnv = exp(-8.0 * ((beat * 2) - (beat * 2).toInt()))
        val pluckLeft = sin(2.0 * PI * pluckFreq * time) * pluckEnv * 0.4
        val pluckRight = sin(2.0 * PI * (pluckFreq * 1.01) * (time + 0.05)) * pluckEnv * 0.4

        return Pair(kick + sub * 0.9 + pluckLeft, kick + sub * 0.9 + pluckRight)
    }

    private fun synthOrchestral(time: Double, beat: Double): Pair<Double, Double> {
        // Warm Cello & Strings harmonic chords (C minor: C, Eb, G, Bb)
        val c2 = 65.41
        val eb2 = 77.78
        val g2 = 98.0
        val bb2 = 116.54

        val vibrato = 1.0 + 0.015 * sin(2.0 * PI * 5.0 * time)

        val cello1 = sin(2.0 * PI * c2 * vibrato * time) + 0.5 * sin(4.0 * PI * c2 * time)
        val cello2 = sin(2.0 * PI * eb2 * vibrato * time) + 0.4 * sin(4.0 * PI * eb2 * time)
        val violin1 = sin(2.0 * PI * g2 * 2.0 * vibrato * time) * 0.6
        val violin2 = sin(2.0 * PI * bb2 * 2.0 * vibrato * time) * 0.5

        val lfo = 0.5 + 0.5 * sin(0.3 * time)
        val l = (cello1 + violin1 * 0.7) * (0.6 + 0.4 * lfo)
        val r = (cello2 + violin2 * 0.7) * (0.6 + 0.4 * (1.0 - lfo))
        return Pair(l, r)
    }

    private fun synthJazzRhodes(time: Double, beat: Double, beatFrac: Double): Pair<Double, Double> {
        // Electric piano / Rhodes bell-like harmonic tones
        val chordIdx = (beat / 8).toInt() % 4
        val baseFreq = when (chordIdx) {
            0 -> 261.63 // C4
            1 -> 220.00 // A3
            2 -> 293.66 // D4
            else -> 196.00 // G3
        }

        val bell = sin(2.0 * PI * baseFreq * time) * exp(-2.0 * beatFrac) +
                0.5 * sin(2.0 * PI * (baseFreq * 2) * time) * exp(-3.5 * beatFrac) +
                0.25 * sin(2.0 * PI * (baseFreq * 4) * time) * exp(-5.0 * beatFrac)

        // Walking jazz upright bass
        val bassStep = (beat.toInt() % 4)
        val bassFreq = (baseFreq / 4) * (1.0 + bassStep * 0.15)
        val bass = sin(2.0 * PI * bassFreq * time) * exp(-1.5 * beatFrac) * 0.8

        return Pair(bell * 0.7 + bass * 0.8, bell * 0.75 + bass * 0.7)
    }

    private fun synthAmbientFlow(time: Double, beat: Double): Pair<Double, Double> {
        // 432Hz healing ambient harmonic drone
        val f1 = 432.0 / 4 // 108 Hz
        val f2 = 432.0 / 3 // 144 Hz
        val f3 = 432.0 / 2 // 216 Hz

        val slowPhaser1 = sin(0.15 * time)
        val slowPhaser2 = cos(0.22 * time)

        val droneL = sin(2.0 * PI * f1 * time) * 0.5 + sin(2.0 * PI * (f2 + slowPhaser1 * 2) * time) * 0.4
        val droneR = sin(2.0 * PI * f1 * time) * 0.5 + sin(2.0 * PI * (f3 + slowPhaser2 * 2) * time) * 0.4

        return Pair(droneL, droneR)
    }
}
