package com.example.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.db.BookmarkEntity
import com.example.data.db.EqualizerPresetEntity
import com.example.data.db.PlaylistEntity
import com.example.data.db.PlaylistTrackCrossRef
import com.example.data.db.TrackEntity
import com.example.data.model.AudioBookmark
import com.example.data.model.EqualizerPreset
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.player.SynthAudioGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class MusicRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val trackDao = db.trackDao()
    private val playlistDao = db.playlistDao()
    private val equalizerDao = db.equalizerDao()
    private val bookmarkDao = db.bookmarkDao()

    val allTracks: Flow<List<Track>> = trackDao.getAllTracks().map { list -> list.map { it.toModel() } }
    val favoriteTracks: Flow<List<Track>> = trackDao.getFavoriteTracks().map { list -> list.map { it.toModel() } }
    val recentlyPlayed: Flow<List<Track>> = trackDao.getRecentlyPlayed().map { list -> list.map { it.toModel() } }
    val mostPlayed: Flow<List<Track>> = trackDao.getMostPlayed().map { list -> list.map { it.toModel() } }
    val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists().map { list ->
        list.map { entity ->
            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                createdTimestamp = entity.createdTimestamp
            )
        }
    }
    val allPresets: Flow<List<EqualizerPreset>> = equalizerDao.getAllPresets().map { list ->
        list.map { it.toModel() }
    }

    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return playlistDao.getTracksForPlaylist(playlistId).map { list -> list.map { it.toModel() } }
    }

    suspend fun initDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val existingTracks = trackDao.getAllTracks().firstOrNull()
        if (existingTracks.isNullOrEmpty()) {
            // Generate audio files for offline demo
            val generatedPaths = SynthAudioGenerator.ensureDemoTracksExist(context)

            val seedTracks = listOf(
                TrackEntity(
                    id = 1001L,
                    title = "Neon Drift (Cyberwave Odyssey)",
                    artist = "AuraPulse Synth",
                    album = "Neon City Lights",
                    durationMs = 90_000L,
                    path = generatedPaths["neon_drift"] ?: "",
                    uriString = generatedPaths["neon_drift"] ?: "",
                    format = "FLAC",
                    sampleRate = 96000,
                    bitDepth = 24,
                    bitrateKbps = 1411,
                    albumArtUri = null,
                    albumArtResId = R.drawable.cover_synthwave,
                    isFavorite = true,
                    playCount = 12,
                    lastPlayedTimestamp = System.currentTimeMillis() - 3600_000L,
                    year = 2024,
                    genre = "Synthwave / Cyberpunk",
                    folder = "/Music/Hi-Res",
                    lyrics = "[00:04.00]Cruising under neon violet skies\n[00:09.50]Analog pulse that never dies\n[00:15.00]Synthesizer bass begins to hum\n[00:20.50]To the rhythm of the city drum\n[00:28.00]Speeding past the holographic lights\n[00:36.00]Lost in endless cyber nights"
                ),
                TrackEntity(
                    id = 1002L,
                    title = "Eclipse Resonance",
                    artist = "The Celestial Strings",
                    album = "Audiophile Concertos",
                    durationMs = 90_000L,
                    path = generatedPaths["eclipse_resonance"] ?: "",
                    uriString = generatedPaths["eclipse_resonance"] ?: "",
                    format = "DSD",
                    sampleRate = 192000,
                    bitDepth = 24,
                    bitrateKbps = 5645,
                    albumArtUri = null,
                    albumArtResId = R.drawable.cover_audiophile,
                    isFavorite = true,
                    playCount = 8,
                    lastPlayedTimestamp = System.currentTimeMillis() - 7200_000L,
                    year = 2023,
                    genre = "Classical / Orchestral",
                    folder = "/Music/Audiophile Master",
                    lyrics = "[00:05.00]Cello strings resonate through the hall\n[00:14.00]Harmonic depth against the silence\n[00:24.00]Pure acoustic warmth and gold\n[00:35.00]Every vibration rich and bold"
                ),
                TrackEntity(
                    id = 1003L,
                    title = "Subfractal Void",
                    artist = "Synaptic Void",
                    album = "Subfractal Bass",
                    durationMs = 90_000L,
                    path = generatedPaths["subfractal_void"] ?: "",
                    uriString = generatedPaths["subfractal_void"] ?: "",
                    format = "WAV",
                    sampleRate = 96000,
                    bitDepth = 32,
                    bitrateKbps = 9216,
                    albumArtUri = null,
                    albumArtResId = R.drawable.cover_electronic,
                    isFavorite = false,
                    playCount = 15,
                    lastPlayedTimestamp = System.currentTimeMillis() - 14400_000L,
                    year = 2024,
                    genre = "Sub-Bass Electronic",
                    folder = "/Music/Electronic",
                    lyrics = "[00:02.00]Frequency dropping to 40 Hertz\n[00:08.00]Sub-bass impact shattering the earth\n[00:16.00]Digital glitch and analog drive\n[00:24.00]Feel the sound system come alive"
                ),
                TrackEntity(
                    id = 1004L,
                    title = "Midnight Horizon",
                    artist = "Miles & Velvet",
                    album = "Late Night Sessions",
                    durationMs = 90_000L,
                    path = generatedPaths["midnight_horizon"] ?: "",
                    uriString = generatedPaths["midnight_horizon"] ?: "",
                    format = "FLAC",
                    sampleRate = 88200,
                    bitDepth = 24,
                    bitrateKbps = 1250,
                    albumArtUri = null,
                    albumArtResId = R.drawable.cover_audiophile,
                    isFavorite = true,
                    playCount = 5,
                    lastPlayedTimestamp = System.currentTimeMillis() - 28800_000L,
                    year = 2022,
                    genre = "Jazz Fusion",
                    folder = "/Music/Jazz",
                    lyrics = "[00:06.00]Warm Rhodes keys in the dim lit room\n[00:15.00]Chasing away the midnight gloom\n[00:26.00]Smooth upright bass taking the lead\n[00:38.00]Everything an audiophile could need"
                ),
                TrackEntity(
                    id = 1005L,
                    title = "Solaris Ambient Flow",
                    artist = "Cosmic Drift",
                    album = "Atmospheres Vol. 1",
                    durationMs = 90_000L,
                    path = generatedPaths["solaris_ambient"] ?: "",
                    uriString = generatedPaths["solaris_ambient"] ?: "",
                    format = "ALAC",
                    sampleRate = 96000,
                    bitDepth = 24,
                    bitrateKbps = 1320,
                    albumArtUri = null,
                    albumArtResId = R.drawable.cover_synthwave,
                    isFavorite = false,
                    playCount = 3,
                    lastPlayedTimestamp = System.currentTimeMillis() - 86400_000L,
                    year = 2024,
                    genre = "Ambient / Meditative",
                    folder = "/Music/Hi-Res",
                    lyrics = "[00:10.00]432 Hertz harmonic wave\n[00:25.00]Gentle cosmic peace you crave\n[00:40.00]Infinite space and stereo spread\n[00:55.00]Harmonies floating in your head"
                )
            )

            trackDao.insertAll(seedTracks)

            // Seed initial Poweramp equalizer presets
            val defaultPresets = listOf(
                EqualizerPresetEntity(
                    name = "Flat (Direct Master)",
                    isBuiltIn = true,
                    preampGain = 0f,
                    bassBoost = 0f,
                    trebleBoost = 0f,
                    virtualizerStrength = 0f,
                    bandGains = "0,0,0,0,0,0,0,0,0,0"
                ),
                EqualizerPresetEntity(
                    name = "Poweramp Bass Boost",
                    isBuiltIn = true,
                    preampGain = -1.5f,
                    bassBoost = 75f,
                    trebleBoost = 2f,
                    virtualizerStrength = 20f,
                    bandGains = "9.0,7.5,5.0,2.0,0.0,-1.0,0.5,1.5,3.0,4.0"
                ),
                EqualizerPresetEntity(
                    name = "Rock & Metal",
                    isBuiltIn = true,
                    preampGain = -1.0f,
                    bassBoost = 40f,
                    trebleBoost = 4f,
                    virtualizerStrength = 15f,
                    bandGains = "6.0,4.5,2.0,-1.0,-2.5,0.0,2.5,4.0,5.5,6.5"
                ),
                EqualizerPresetEntity(
                    name = "Electronic & Club",
                    isBuiltIn = true,
                    preampGain = -2.0f,
                    bassBoost = 85f,
                    trebleBoost = 3f,
                    virtualizerStrength = 35f,
                    bandGains = "8.0,6.0,3.0,0.0,-2.0,1.0,3.0,5.0,7.0,8.0"
                ),
                EqualizerPresetEntity(
                    name = "Vocal Clarity",
                    isBuiltIn = true,
                    preampGain = 0.5f,
                    bassBoost = 0f,
                    trebleBoost = 3f,
                    virtualizerStrength = 10f,
                    bandGains = "-4.0,-2.0,0.0,2.5,5.0,6.0,4.5,3.0,1.5,0.0"
                ),
                EqualizerPresetEntity(
                    name = "Acoustic & Classical",
                    isBuiltIn = true,
                    preampGain = 0f,
                    bassBoost = 20f,
                    trebleBoost = 2f,
                    virtualizerStrength = 40f,
                    bandGains = "3.5,2.5,1.5,0.5,0.0,1.0,2.0,3.0,4.0,4.5"
                ),
                EqualizerPresetEntity(
                    name = "Wide Stereo Hall",
                    isBuiltIn = true,
                    preampGain = -0.5f,
                    bassBoost = 30f,
                    trebleBoost = 1f,
                    virtualizerStrength = 90f,
                    bandGains = "4.0,2.0,0.0,0.0,1.0,1.5,2.0,3.0,4.5,5.0"
                )
            )

            for (preset in defaultPresets) {
                equalizerDao.insertPreset(preset)
            }

            // Seed initial playlists
            val p1Id = playlistDao.insertPlaylist(
                PlaylistEntity(name = "Hi-Res Audiophile Gems", description = "24-bit studio master recordings")
            )
            val p2Id = playlistDao.insertPlaylist(
                PlaylistEntity(name = "Cyberpunk & Bass", description = "Heavy electronic synth frequencies")
            )

            playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(p1Id, 1001L, 0))
            playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(p1Id, 1002L, 1))
            playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(p1Id, 1004L, 2))

            playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(p2Id, 1001L, 0))
            playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(p2Id, 1003L, 1))
        }
    }

    suspend fun scanDeviceMedia() = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.YEAR
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )

        val scannedTracks = mutableListOf<TrackEntity>()

        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val mimeCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val yearCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val title = it.getString(titleCol) ?: "Unknown Title"
                val artist = it.getString(artistCol) ?: "Unknown Artist"
                val album = it.getString(albumCol) ?: "Unknown Album"
                val duration = it.getLong(durationCol)
                val path = it.getString(dataCol) ?: ""
                val mime = it.getString(mimeCol) ?: "audio/mpeg"
                val year = it.getInt(yearCol)

                val file = File(path)
                val folder = file.parent ?: "/Music"
                val format = when {
                    path.endsWith(".flac", ignoreCase = true) -> "FLAC"
                    path.endsWith(".wav", ignoreCase = true) -> "WAV"
                    path.endsWith(".dsd", ignoreCase = true) || path.endsWith(".dff", ignoreCase = true) -> "DSD"
                    path.endsWith(".m4a", ignoreCase = true) || path.endsWith(".aac", ignoreCase = true) -> "AAC"
                    path.endsWith(".ogg", ignoreCase = true) -> "OGG"
                    path.endsWith(".alac", ignoreCase = true) -> "ALAC"
                    else -> "MP3"
                }

                val sampleRate = if (format in listOf("FLAC", "WAV", "DSD")) 96000 else 44100
                val bitDepth = if (format in listOf("FLAC", "WAV")) 24 else 16
                val bitrate = if (format in listOf("FLAC", "WAV")) 1411 else 320

                val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                scannedTracks.add(
                    TrackEntity(
                        id = id,
                        title = title,
                        artist = artist,
                        album = album,
                        durationMs = duration,
                        path = path,
                        uriString = contentUri.toString(),
                        format = format,
                        sampleRate = sampleRate,
                        bitDepth = bitDepth,
                        bitrateKbps = bitrate,
                        albumArtUri = null,
                        albumArtResId = null,
                        isFavorite = false,
                        playCount = 0,
                        lastPlayedTimestamp = 0L,
                        year = if (year > 0) year else 2024,
                        genre = "Local Audio",
                        folder = folder,
                        lyrics = null
                    )
                )
            }
        }

        if (scannedTracks.isNotEmpty()) {
            trackDao.insertAll(scannedTracks)
        }
    }

    suspend fun toggleFavorite(trackId: Long, current: Boolean) {
        withContext(Dispatchers.IO) {
            trackDao.updateFavorite(trackId, !current)
        }
    }

    suspend fun recordPlay(trackId: Long) {
        withContext(Dispatchers.IO) {
            trackDao.recordPlay(trackId, System.currentTimeMillis())
        }
    }

    suspend fun updateTrackTags(id: Long, title: String, artist: String, album: String, genre: String, year: Int) {
        withContext(Dispatchers.IO) {
            trackDao.updateTags(id, title, artist, album, genre, year)
        }
    }

    suspend fun updateTrackLyrics(id: Long, lyrics: String) {
        withContext(Dispatchers.IO) {
            trackDao.updateLyrics(id, lyrics)
        }
    }

    suspend fun createPlaylist(name: String, description: String): Long {
        return withContext(Dispatchers.IO) {
            playlistDao.insertPlaylist(PlaylistEntity(name = name, description = description))
        }
    }

    suspend fun deletePlaylist(id: Long) {
        withContext(Dispatchers.IO) {
            playlistDao.deletePlaylist(id)
        }
    }

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO) {
            val count = playlistDao.getTrackCountForPlaylist(playlistId).firstOrNull() ?: 0
            playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(playlistId, trackId, count))
        }
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO) {
            playlistDao.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    suspend fun saveEqualizerPreset(preset: EqualizerPreset): Long {
        return withContext(Dispatchers.IO) {
            equalizerDao.insertPreset(
                EqualizerPresetEntity(
                    id = if (preset.id > 0) preset.id else 0,
                    name = preset.name,
                    isBuiltIn = false,
                    preampGain = preset.preampGain,
                    bassBoost = preset.bassBoost,
                    trebleBoost = preset.trebleBoost,
                    virtualizerStrength = preset.virtualizerStrength,
                    bandGains = preset.bandGains.joinToString(",") { "%.1f".format(it) }
                )
            )
        }
    }

    suspend fun deleteEqualizerPreset(id: Long) {
        withContext(Dispatchers.IO) {
            equalizerDao.deletePreset(id)
        }
    }

    fun getBookmarksForTrack(trackId: Long): Flow<List<AudioBookmark>> {
        return bookmarkDao.getBookmarksForTrack(trackId).map { list ->
            list.map { AudioBookmark(id = it.id, trackId = it.trackId, positionMs = it.positionMs, label = it.label, createdTimestamp = it.createdTimestamp) }
        }
    }

    suspend fun addBookmark(trackId: Long, positionMs: Long, label: String): Long {
        return withContext(Dispatchers.IO) {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    trackId = trackId,
                    positionMs = positionMs,
                    label = label
                )
            )
        }
    }

    suspend fun deleteBookmark(id: Long) {
        withContext(Dispatchers.IO) {
            bookmarkDao.deleteBookmark(id)
        }
    }

    suspend fun addTracksToPlaylist(playlistId: Long, trackIds: List<Long>) {
        withContext(Dispatchers.IO) {
            val count = playlistDao.getTrackCountForPlaylist(playlistId).firstOrNull() ?: 0
            trackIds.forEachIndexed { idx, tId ->
                playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(playlistId, tId, count + idx))
            }
        }
    }

    private fun TrackEntity.toModel(): Track {
        return Track(
            id = id,
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs,
            path = path,
            uriString = uriString,
            format = format,
            sampleRate = sampleRate,
            bitDepth = bitDepth,
            bitrateKbps = bitrateKbps,
            albumArtUri = albumArtUri,
            albumArtResId = albumArtResId,
            isFavorite = isFavorite,
            playCount = playCount,
            lastPlayedTimestamp = lastPlayedTimestamp,
            year = year,
            genre = genre,
            folder = folder,
            lyrics = lyrics
        )
    }

    private fun EqualizerPresetEntity.toModel(): EqualizerPreset {
        val gains = bandGains.split(",").mapNotNull { it.trim().toFloatOrNull() }
        val tenGains = if (gains.size >= 10) gains.take(10) else gains + List(10 - gains.size) { 0f }
        return EqualizerPreset(
            id = id,
            name = name,
            isBuiltIn = isBuiltIn,
            preampGain = preampGain,
            bassBoost = bassBoost,
            trebleBoost = trebleBoost,
            virtualizerStrength = virtualizerStrength,
            bandGains = tenGains
        )
    }
}
