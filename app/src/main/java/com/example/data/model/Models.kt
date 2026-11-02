package com.example.data.model

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val path: String,
    val uriString: String,
    val format: String = "FLAC", // FLAC, WAV, DSD, ALAC, MP3, AAC
    val sampleRate: Int = 96000, // in Hz: 44100, 48000, 96000, 192000
    val bitDepth: Int = 24, // in bits: 16, 24, 32
    val bitrateKbps: Int = 1411,
    val albumArtUri: String? = null,
    val albumArtResId: Int? = null,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L,
    val year: Int = 2024,
    val genre: String = "Audiophile",
    val folder: String = "/Music/Hi-Res",
    val lyrics: String? = null
) {
    val isHiRes: Boolean
        get() = bitDepth >= 24 || sampleRate >= 48000 || format.uppercase() in listOf("FLAC", "DSD", "WAV", "ALAC")

    val durationFormatted: String
        get() {
            val totalSec = durationMs / 1000
            val minutes = totalSec / 60
            val seconds = totalSec % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

data class Playlist(
    val id: Long,
    val name: String,
    val description: String = "",
    val trackCount: Int = 0,
    val createdTimestamp: Long = System.currentTimeMillis()
)

data class EqualizerPreset(
    val id: Long,
    val name: String,
    val isBuiltIn: Boolean = false,
    val preampGain: Float = 0f, // -6dB to +6dB
    val bassBoost: Float = 0f, // 0% to 100%
    val trebleBoost: Float = 0f, // -10dB to +10dB
    val virtualizerStrength: Float = 0f, // 0% to 100%
    val bandGains: List<Float> = List(10) { 0f } // 10 bands (-12dB to +12dB)
)

enum class RepeatMode {
    OFF, ALL, ONE
}

enum class VisualizerStyle(val displayName: String) {
    SPECTRUM_BARS("Spectrum FFT"),
    WAVEFORM_OSCILLOSCOPE("Oscilloscope"),
    ANALOG_VU_METER("Analog VU Meter"),
    CIRCULAR_PULSE("Pulse Ring")
}

enum class SortType(val displayName: String) {
    TITLE("Song Title"),
    ARTIST("Artist"),
    ALBUM("Album"),
    DURATION("Duration"),
    BITRATE("Quality (Hi-Res)")
}

enum class LibraryTab(val displayName: String) {
    TRACKS("Tracks"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    FOLDERS("Folders"),
    PLAYLISTS("Playlists"),
    HI_RES("Hi-Res Only")
}

data class SpatialAudioConfig(
    val isEnabled: Boolean = false,
    val azimuthDeg: Float = 0f, // 0 = Center/Front, 90 = Right, 180 = Rear, 270 = Left
    val soundstageWidth: Float = 175f, // 100% to 300%
    val depth: Float = 60f, // 0% to 100%
    val elevationDeg: Float = 0f, // -45 to +45 degrees
    val room: SpatialRoom = SpatialRoom.CONCERT_HALL,
    val isAutoOrbit: Boolean = false,
    val orbitSpeed: Float = 1.0f // 0.2x to 3.0x
)

enum class SpatialRoom(val title: String, val subtitle: String) {
    STUDIO("Binaural Studio", "Dry reference bit-perfect imaging"),
    CONCERT_HALL("Concert Hall 360", "Lush acoustic surround reverberation"),
    CINEMA_SURROUND("Cinema 7.1.4", "Deep cinematic spatial immersion"),
    OPEN_ARENA("Open-Air Arena", "Expansive 360° stadium soundfield"),
    INTIMATE_LOUNGE("Jazz Lounge", "Warm close-proximity club acoustics")
}

data class AudioBookmark(
    val id: Long = 0L,
    val trackId: Long,
    val positionMs: Long,
    val label: String,
    val createdTimestamp: Long = System.currentTimeMillis()
) {
    val positionFormatted: String
        get() {
            val totalSec = positionMs / 1000
            return "%d:%02d".format(totalSec / 60, totalSec % 60)
        }
}

data class ABLoop(
    val pointA: Long? = null,
    val pointB: Long? = null,
    val isEnabled: Boolean = false
) {
    val isActive: Boolean
        get() = isEnabled && pointA != null && pointB != null && pointB > pointA
}

enum class ThemeAccent(val displayName: String, val hexColor: Long) {
    NEON_CYAN("Cyan Surge", 0xFF00F5D4),
    ELECTRIC_VIOLET("Ultra Violet", 0xFF9D4EDD),
    HI_RES_GOLD("Audiophile Gold", 0xFFFFBE0B),
    AMBER_TUBE("Warm Amber", 0xFFFB5607),
    NEON_PINK("Cyber Pink", 0xFFFF2A85),
    MATRIX_GREEN("Emerald Hi-Fi", 0xFF06D6A0)
}

data class PlaybackSettings(
    val crossfadeSeconds: Int = 0, // 0 = off, 2, 4, 6, 8, 10
    val isGapless: Boolean = true,
    val balance: Float = 0f, // -1.0 (Left) to +1.0 (Right)
    val isMono: Boolean = false,
    val replayGainEnabled: Boolean = true,
    val pauseOnHeadsetDisconnect: Boolean = true,
    val duckOnNotification: Boolean = true,
    val resumeOnLaunch: Boolean = true,
    val accent: ThemeAccent = ThemeAccent.NEON_CYAN
)

data class ListeningStats(
    val totalPlayCount: Int,
    val totalTimeListenedMs: Long,
    val uniqueTracksPlayed: Int,
    val topTracks: List<Track>,
    val topArtists: List<Pair<String, Int>>
)


