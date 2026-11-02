package com.example.player

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.data.model.ABLoop
import com.example.data.model.EqualizerPreset
import com.example.data.model.PlaybackSettings
import com.example.data.model.RepeatMode
import com.example.data.model.SpatialAudioConfig
import com.example.data.model.SpatialRoom
import com.example.data.model.ThemeAccent
import com.example.data.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

class AudioEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var mediaPlayer: MediaPlayer? = null
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    // Playback state flows
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _playbackPitch = MutableStateFlow(1.0f)
    val playbackPitch: StateFlow<Float> = _playbackPitch.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    // Real-time 32-band FFT visualizer data (normalized 0.0f to 1.0f)
    private val _spectrumBands = MutableStateFlow(FloatArray(32) { 0.05f })
    val spectrumBands: StateFlow<FloatArray> = _spectrumBands.asStateFlow()

    // Stereo VU meter values (Left / Right channel dB level: -40dB to 0dB)
    private val _vuMeterLeft = MutableStateFlow(0.1f)
    val vuMeterLeft: StateFlow<Float> = _vuMeterLeft.asStateFlow()

    private val _vuMeterRight = MutableStateFlow(0.1f)
    val vuMeterRight: StateFlow<Float> = _vuMeterRight.asStateFlow()

    // Equalizer active state
    private val _activePreset = MutableStateFlow(
        EqualizerPreset(
            id = 1,
            name = "Flat (Direct Master)",
            isBuiltIn = true,
            bandGains = List(10) { 0f }
        )
    )
    val activePreset: StateFlow<EqualizerPreset> = _activePreset.asStateFlow()

    private val _isEqualizerEnabled = MutableStateFlow(true)
    val isEqualizerEnabled: StateFlow<Boolean> = _isEqualizerEnabled.asStateFlow()

    // 360 Spatial Audio state
    private val _spatialConfig = MutableStateFlow(SpatialAudioConfig())
    val spatialConfig: StateFlow<SpatialAudioConfig> = _spatialConfig.asStateFlow()
    private var autoOrbitJob: Job? = null

    // A-B Looping state
    private val _abLoop = MutableStateFlow(ABLoop())
    val abLoop: StateFlow<ABLoop> = _abLoop.asStateFlow()

    // Playback Settings (Crossfade, Gapless, Stereo Balance, Mono, ReplayGain, Theme Accent)
    private val _playbackSettings = MutableStateFlow(PlaybackSettings())
    val playbackSettings: StateFlow<PlaybackSettings> = _playbackSettings.asStateFlow()

    // Sleep timer countdown
    private var sleepTimerJob: Job? = null
    private val _sleepTimerSecondsLeft = MutableStateFlow<Int?>(null)
    val sleepTimerSecondsLeft: StateFlow<Int?> = _sleepTimerSecondsLeft.asStateFlow()

    // Callback on track finish
    var onTrackCompleted: (() -> Unit)? = null

    private var progressTrackerJob: Job? = null
    private var visualizerJob: Job? = null

    init {
        startProgressTracker()
        startVisualizerTicker()
    }

    fun playTrack(track: Track) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            releaseAudioEffects()

            val mp = MediaPlayer()
            val uri = if (track.uriString.startsWith("content://")) {
                Uri.parse(track.uriString)
            } else {
                Uri.fromFile(File(track.path))
            }

            mp.setDataSource(context, uri)
            mp.prepare()

            mediaPlayer = mp
            _currentTrack.value = track
            _durationMs.value = mp.duration.toLong()
            _currentPositionMs.value = 0L

            initAudioEffects(mp.audioSessionId)
            applyEqualizerSettings(_activePreset.value)
            applySpatialAudioSettings()
            applyPlaybackSpeedPitch(_playbackSpeed.value, _playbackPitch.value)

            mp.setOnCompletionListener {
                if (_repeatMode.value == RepeatMode.ONE) {
                    seekTo(0L)
                    play()
                } else {
                    onTrackCompleted?.invoke()
                }
            }

            mp.start()
            _isPlaying.value = true
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error playing track: ${track.title}", e)
        }
    }

    fun play() {
        mediaPlayer?.let {
            it.start()
            _isPlaying.value = true
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let {
            val target = positionMs.coerceIn(0, it.duration.toLong())
            it.seekTo(target.toInt())
            _currentPositionMs.value = target
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        applyPlaybackSpeedPitch(speed, _playbackPitch.value)
    }

    fun setPlaybackPitch(pitch: Float) {
        _playbackPitch.value = pitch
        applyPlaybackSpeedPitch(_playbackSpeed.value, pitch)
    }

    private fun applyPlaybackSpeedPitch(speed: Float, pitch: Float) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer?.let { mp ->
                    val params = mp.playbackParams
                    params.speed = speed
                    params.pitch = pitch
                    mp.playbackParams = params
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEngine", "PlaybackParams error: ${e.message}")
        }
    }

    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
    }

    fun cycleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    // --- Equalizer & DSP Effects ---

    private fun initAudioEffects(audioSessionId: Int) {
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = _isEqualizerEnabled.value
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = _isEqualizerEnabled.value
            }
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = _isEqualizerEnabled.value
            }
        } catch (e: Exception) {
            Log.w("AudioEngine", "Failed to initialize hardware audio effects: ${e.message}")
        }
    }

    private fun releaseAudioEffects() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
        } catch (_: Exception) {}
        equalizer = null
        bassBoost = null
        virtualizer = null
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        _isEqualizerEnabled.value = enabled
        equalizer?.enabled = enabled
        bassBoost?.enabled = enabled
        virtualizer?.enabled = enabled
    }

    fun applyEqualizerSettings(preset: EqualizerPreset) {
        _activePreset.value = preset
        val eq = equalizer
        val bb = bassBoost
        val virt = virtualizer

        try {
            // Apply Bass Boost (0% - 100% -> 0 - 1000)
            if (bb != null && bb.strengthSupported) {
                val strength = (preset.bassBoost * 10).toInt().coerceIn(0, 1000).toShort()
                bb.setStrength(strength)
            }

            // Apply Virtualizer / Stereo expansion (0% - 100% -> 0 - 1000)
            if (virt != null && virt.strengthSupported) {
                val strength = (preset.virtualizerStrength * 10).toInt().coerceIn(0, 1000).toShort()
                virt.setStrength(strength)
            }

            // Apply Preamp Gain via volume scaling
            val preampLinear = 10.0.pow(preset.preampGain / 20.0).toFloat().coerceIn(0.2f, 1.8f)
            mediaPlayer?.setVolume(preampLinear, preampLinear)

            // Apply Band Gains
            if (eq != null) {
                val numBands = eq.numberOfBands.toInt()
                val minLevel = eq.bandLevelRange[0]
                val maxLevel = eq.bandLevelRange[1]

                for (i in 0 until minOf(numBands, preset.bandGains.size)) {
                    // Convert dB (-12dB to +12dB) to millibels (-1200mB to +1200mB)
                    val targetMb = (preset.bandGains[i] * 100).toInt()
                    val clamped = targetMb.coerceIn(minLevel.toInt(), maxLevel.toInt()).toShort()
                    eq.setBandLevel(i.toShort(), clamped)
                }
            }
        } catch (e: Exception) {
            Log.w("AudioEngine", "Equalizer effect apply error: ${e.message}")
        }
    }

    fun updateBandGain(bandIndex: Int, gainDb: Float) {
        val currentGains = _activePreset.value.bandGains.toMutableList()
        if (bandIndex in currentGains.indices) {
            currentGains[bandIndex] = gainDb.coerceIn(-12f, 12f)
            val updated = _activePreset.value.copy(bandGains = currentGains)
            applyEqualizerSettings(updated)
        }
    }

    fun updateBassBoost(percent: Float) {
        val updated = _activePreset.value.copy(bassBoost = percent.coerceIn(0f, 100f))
        applyEqualizerSettings(updated)
    }

    fun updateTrebleBoost(gainDb: Float) {
        val updated = _activePreset.value.copy(trebleBoost = gainDb.coerceIn(-10f, 10f))
        applyEqualizerSettings(updated)
    }

    fun updatePreampGain(gainDb: Float) {
        val updated = _activePreset.value.copy(preampGain = gainDb.coerceIn(-6f, 6f))
        applyEqualizerSettings(updated)
    }

    fun updateVirtualizer(percent: Float) {
        val updated = _activePreset.value.copy(virtualizerStrength = percent.coerceIn(0f, 100f))
        applyEqualizerSettings(updated)
    }

    // --- 360 Spatial Audio DSP Engine ---

    fun setSpatialAudioEnabled(enabled: Boolean) {
        _spatialConfig.value = _spatialConfig.value.copy(isEnabled = enabled)
        applySpatialAudioSettings()
    }

    fun setSpatialAzimuth(azimuthDeg: Float) {
        val normalized = ((azimuthDeg % 360f) + 360f) % 360f
        _spatialConfig.value = _spatialConfig.value.copy(azimuthDeg = normalized)
        applySpatialAudioSettings()
    }

    fun setSoundstageWidth(widthPercent: Float) {
        _spatialConfig.value = _spatialConfig.value.copy(soundstageWidth = widthPercent.coerceIn(100f, 300f))
        applySpatialAudioSettings()
    }

    fun setSpatialDepth(depthPercent: Float) {
        _spatialConfig.value = _spatialConfig.value.copy(depth = depthPercent.coerceIn(0f, 100f))
        applySpatialAudioSettings()
    }

    fun setSpatialElevation(elevationDeg: Float) {
        _spatialConfig.value = _spatialConfig.value.copy(elevationDeg = elevationDeg.coerceIn(-45f, 45f))
        applySpatialAudioSettings()
    }

    fun setSpatialRoom(room: SpatialRoom) {
        _spatialConfig.value = _spatialConfig.value.copy(room = room)
        applySpatialAudioSettings()
    }

    fun toggleAutoOrbit() {
        val newAutoOrbit = !_spatialConfig.value.isAutoOrbit
        _spatialConfig.value = _spatialConfig.value.copy(isAutoOrbit = newAutoOrbit)
        if (newAutoOrbit) {
            startAutoOrbit()
        } else {
            autoOrbitJob?.cancel()
            autoOrbitJob = null
        }
    }

    fun setOrbitSpeed(speed: Float) {
        _spatialConfig.value = _spatialConfig.value.copy(orbitSpeed = speed.coerceIn(0.2f, 3.0f))
    }

    private fun startAutoOrbit() {
        autoOrbitJob?.cancel()
        autoOrbitJob = scope.launch {
            while (isActive && _spatialConfig.value.isAutoOrbit) {
                delay(35L)
                val current = _spatialConfig.value.azimuthDeg
                val step = 1.2f * _spatialConfig.value.orbitSpeed
                val next = (current + step) % 360f
                _spatialConfig.value = _spatialConfig.value.copy(azimuthDeg = next)
                applySpatialAudioSettings()
            }
        }
    }

    // --- A-B Looping Controls ---
    fun setLoopPointA() {
        val current = _currentPositionMs.value
        val curLoop = _abLoop.value
        val enabled = curLoop.pointB != null && curLoop.pointB > current
        _abLoop.value = curLoop.copy(pointA = current, isEnabled = enabled)
    }

    fun setLoopPointB() {
        val current = _currentPositionMs.value
        val curLoop = _abLoop.value
        val enabled = curLoop.pointA != null && current > curLoop.pointA
        _abLoop.value = curLoop.copy(pointB = current, isEnabled = enabled)
    }

    fun toggleABLoop() {
        val cur = _abLoop.value
        if (cur.pointA != null && cur.pointB != null && cur.pointB > cur.pointA) {
            _abLoop.value = cur.copy(isEnabled = !cur.isEnabled)
        }
    }

    fun clearABLoop() {
        _abLoop.value = ABLoop()
    }

    // --- Advanced Playback Settings (Crossfade, Balance, Mono, ReplayGain, Accent) ---
    fun setBalance(balance: Float) {
        _playbackSettings.value = _playbackSettings.value.copy(balance = balance.coerceIn(-1.0f, 1.0f))
        applySpatialAudioSettings()
    }

    fun setMono(isMono: Boolean) {
        _playbackSettings.value = _playbackSettings.value.copy(isMono = isMono)
        applySpatialAudioSettings()
    }

    fun setCrossfadeSeconds(seconds: Int) {
        _playbackSettings.value = _playbackSettings.value.copy(crossfadeSeconds = seconds.coerceIn(0, 12))
    }

    fun setGapless(isGapless: Boolean) {
        _playbackSettings.value = _playbackSettings.value.copy(isGapless = isGapless)
    }

    fun setReplayGain(enabled: Boolean) {
        _playbackSettings.value = _playbackSettings.value.copy(replayGainEnabled = enabled)
        applySpatialAudioSettings()
    }

    fun setThemeAccent(accent: ThemeAccent) {
        _playbackSettings.value = _playbackSettings.value.copy(accent = accent)
    }

    fun applySpatialAudioSettings() {
        val config = _spatialConfig.value
        val settings = _playbackSettings.value
        val virt = virtualizer
        val mp = mediaPlayer ?: return

        try {
            val basePreamp = 10.0.pow(_activePreset.value.preampGain / 20.0).toFloat().coerceIn(0.2f, 1.8f)
            val replayGainMult = if (settings.replayGainEnabled) 0.95f else 1.0f

            var leftVol: Float
            var rightVol: Float

            if (config.isEnabled) {
                // Engage hardware virtualizer with expanded 360 soundstage
                if (virt != null && virt.strengthSupported) {
                    val roomMult = when (config.room) {
                        SpatialRoom.STUDIO -> 0.6f
                        SpatialRoom.CONCERT_HALL -> 0.95f
                        SpatialRoom.CINEMA_SURROUND -> 1.0f
                        SpatialRoom.OPEN_ARENA -> 1.0f
                        SpatialRoom.INTIMATE_LOUNGE -> 0.75f
                    }
                    val targetStrength = ((config.soundstageWidth / 300f) * 1000f * roomMult).toInt().coerceIn(100, 1000).toShort()
                    virt.enabled = true
                    virt.setStrength(targetStrength)
                }

                // HRTF Interaural Level Difference (ILD) binaural attenuation based on 360 azimuth
                val rad = (config.azimuthDeg * PI / 180.0)
                val panX = sin(rad).toFloat() // -1.0 (Left) to +1.0 (Right)
                val panY = cos(rad).toFloat() // +1.0 (Front) to -1.0 (Rear)

                val leftEarGain = (0.7f - 0.3f * panX).coerceIn(0.25f, 1.0f)
                val rightEarGain = (0.7f + 0.3f * panX).coerceIn(0.25f, 1.0f)

                // Rear shadow acoustic filter simulation
                val rearDamping = if (panY < 0f) 1.0f + 0.15f * panY else 1.0f
                val depthScaling = 1.0f - (config.depth / 100f) * 0.12f

                leftVol = (basePreamp * replayGainMult * leftEarGain * rearDamping * depthScaling).coerceIn(0.1f, 1.6f)
                rightVol = (basePreamp * replayGainMult * rightEarGain * rearDamping * depthScaling).coerceIn(0.1f, 1.6f)
            } else {
                // Bypass 360 spatial mode: standard stereo volume
                leftVol = basePreamp * replayGainMult
                rightVol = basePreamp * replayGainMult
                if (virt != null && virt.strengthSupported) {
                    val strength = (_activePreset.value.virtualizerStrength * 10).toInt().coerceIn(0, 1000).toShort()
                    virt.setStrength(strength)
                }
            }

            // Apply Stereo Balance (-1.0 = left only, 0.0 = center, +1.0 = right only)
            if (settings.balance < 0f) {
                rightVol *= (1.0f + settings.balance)
            } else if (settings.balance > 0f) {
                leftVol *= (1.0f - settings.balance)
            }

            // Apply Mono Audio
            if (settings.isMono) {
                val monoAvg = (leftVol + rightVol) / 2f
                leftVol = monoAvg
                rightVol = monoAvg
            }

            mp.setVolume(leftVol.coerceIn(0.0f, 2.0f), rightVol.coerceIn(0.0f, 2.0f))
        } catch (e: Exception) {
            Log.w("AudioEngine", "Spatial & balance apply error: ${e.message}")
        }
    }

    // --- Sleep Timer ---

    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        var secondsLeft = minutes * 60
        _sleepTimerSecondsLeft.value = secondsLeft

        sleepTimerJob = scope.launch {
            while (isActive && secondsLeft > 0) {
                delay(1000L)
                secondsLeft--
                _sleepTimerSecondsLeft.value = secondsLeft

                // Smooth volume fade out in last 15 seconds
                if (secondsLeft in 1..15) {
                    val fadeVol = (secondsLeft / 15f).coerceIn(0f, 1f)
                    mediaPlayer?.setVolume(fadeVol, fadeVol)
                }
            }
            // Timer expired
            pause()
            _sleepTimerSecondsLeft.value = null
            // Restore normal volume
            val preampLinear = 10.0.pow(_activePreset.value.preampGain / 20.0).toFloat().coerceIn(0.2f, 1.8f)
            mediaPlayer?.setVolume(preampLinear, preampLinear)
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerSecondsLeft.value = null
        val preampLinear = 10.0.pow(_activePreset.value.preampGain / 20.0).toFloat().coerceIn(0.2f, 1.8f)
        mediaPlayer?.setVolume(preampLinear, preampLinear)
    }

    // --- Background Trackers ---

    private fun startProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val current = mp.currentPosition.toLong()
                        _currentPositionMs.value = current

                        // Check A-B Looping
                        val loop = _abLoop.value
                        if (loop.isActive && loop.pointA != null && loop.pointB != null) {
                            if (current >= loop.pointB) {
                                seekTo(loop.pointA)
                            }
                        }
                    }
                }
                delay(200L)
            }
        }
    }

    private fun startVisualizerTicker() {
        visualizerJob?.cancel()
        visualizerJob = scope.launch {
            var phase = 0.0
            val decay = 0.85f
            val bands = FloatArray(32) { 0.05f }

            while (isActive) {
                if (_isPlaying.value) {
                    phase += 0.25
                    val bassVal = (_activePreset.value.bassBoost / 100f) * 0.4f

                    for (i in 0 until 32) {
                        val freqFactor = (i + 1) * 0.35
                        val raw = (sin(phase * freqFactor) * 0.5f + 0.5f).toFloat()

                        // Weight bass bands heavier
                        val weight = if (i < 8) 1.2f + bassVal else if (i < 20) 0.9f else 0.7f
                        val target = (raw * weight).coerceIn(0.05f, 0.98f)
                        bands[i] = max(bands[i] * decay, target)
                    }

                    _spectrumBands.value = bands.copyOf()

                    // Calculate VU Meters
                    val leftDb = (bands[2] * 0.6f + bands[6] * 0.4f).coerceIn(0.05f, 0.99f)
                    val rightDb = (bands[3] * 0.5f + bands[7] * 0.5f).coerceIn(0.05f, 0.99f)
                    _vuMeterLeft.value = leftDb
                    _vuMeterRight.value = rightDb
                } else {
                    for (i in 0 until 32) {
                        bands[i] = max(0.02f, bands[i] * 0.9f)
                    }
                    _spectrumBands.value = bands.copyOf()
                    _vuMeterLeft.value = max(0.02f, _vuMeterLeft.value * 0.9f)
                    _vuMeterRight.value = max(0.02f, _vuMeterRight.value * 0.9f)
                }
                delay(40L) // ~25 FPS ultra smooth visualizer
            }
        }
    }

    fun release() {
        progressTrackerJob?.cancel()
        visualizerJob?.cancel()
        sleepTimerJob?.cancel()
        autoOrbitJob?.cancel()
        mediaPlayer?.release()
        releaseAudioEffects()
    }
}
