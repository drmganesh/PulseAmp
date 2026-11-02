package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.EqualizerPreset
import com.example.data.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PulseAmp", appName)
    }

    @Test
    fun `track hi-res and duration formatting`() {
        val track = Track(
            id = 1,
            title = "Neon Drift",
            artist = "AuraPulse",
            album = "Neon City",
            durationMs = 95_000L,
            path = "/dummy/path.flac",
            uriString = "/dummy/path.flac",
            format = "FLAC",
            sampleRate = 96000,
            bitDepth = 24,
            bitrateKbps = 1411
        )

        assertTrue(track.isHiRes)
        assertEquals("1:35", track.durationFormatted)
    }

    @Test
    fun `ab loop active validation`() {
        val loopInactive = com.example.data.model.ABLoop(pointA = 1000L, pointB = null, isEnabled = true)
        org.junit.Assert.assertFalse(loopInactive.isActive)

        val loopActive = com.example.data.model.ABLoop(pointA = 1000L, pointB = 5000L, isEnabled = true)
        assertTrue(loopActive.isActive)
    }

    @Test
    fun `audio bookmark position formatting`() {
        val bookmark = com.example.data.model.AudioBookmark(
            id = 1,
            trackId = 1001,
            positionMs = 75_000L,
            label = "Guitar Solo"
        )
        assertEquals("1:15", bookmark.positionFormatted)
    }

    @Test
    fun `equalizer preset 10 bands default`() {
        val preset = EqualizerPreset(
            id = 1,
            name = "Flat",
            isBuiltIn = true
        )

        assertEquals(10, preset.bandGains.size)
        assertEquals(0f, preset.preampGain, 0.001f)
    }

    @Test
    fun `spatial 360 audio config test`() {
        val config = com.example.data.model.SpatialAudioConfig(
            isEnabled = true,
            azimuthDeg = 90f,
            soundstageWidth = 200f
        )

        assertTrue(config.isEnabled)
        assertEquals(90f, config.azimuthDeg, 0.001f)
        assertEquals(200f, config.soundstageWidth, 0.001f)
    }
}
