package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MusicRepository
import com.example.data.model.ABLoop
import com.example.data.model.AudioBookmark
import com.example.data.model.EqualizerPreset
import com.example.data.model.LibraryTab
import com.example.data.model.ListeningStats
import com.example.data.model.PlaybackSettings
import com.example.data.model.Playlist
import com.example.data.model.RepeatMode
import com.example.data.model.SortType
import com.example.data.model.SpatialAudioConfig
import com.example.data.model.SpatialRoom
import com.example.data.model.ThemeAccent
import com.example.data.model.Track
import com.example.data.model.VisualizerStyle
import com.example.player.AudioEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicRepository(application)
    val audioEngine = AudioEngine(application)

    // Flow from Room repository
    val allTracks: StateFlow<List<Track>> = repository.allTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTracks: StateFlow<List<Track>> = repository.favoriteTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlaylists: StateFlow<List<Playlist>> = repository.allPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPresets: StateFlow<List<EqualizerPreset>> = repository.allPresets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state
    private val _currentTab = MutableStateFlow(LibraryTab.TRACKS)
    val currentTab: StateFlow<LibraryTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.TITLE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    private val _isEqualizerVisible = MutableStateFlow(false)
    val isEqualizerVisible: StateFlow<Boolean> = _isEqualizerVisible.asStateFlow()

    private val _isSpatialAudioVisible = MutableStateFlow(false)
    val isSpatialAudioVisible: StateFlow<Boolean> = _isSpatialAudioVisible.asStateFlow()

    private val _isQueueSheetVisible = MutableStateFlow(false)
    val isQueueSheetVisible: StateFlow<Boolean> = _isQueueSheetVisible.asStateFlow()

    private val _isSleepTimerDialogVisible = MutableStateFlow(false)
    val isSleepTimerDialogVisible: StateFlow<Boolean> = _isSleepTimerDialogVisible.asStateFlow()

    private val _isTagEditorVisible = MutableStateFlow(false)
    val isTagEditorVisible: StateFlow<Boolean> = _isTagEditorVisible.asStateFlow()
    val trackForTagEditing = MutableStateFlow<Track?>(null)

    private val _isCreatePlaylistDialogVisible = MutableStateFlow(false)
    val isCreatePlaylistDialogVisible: StateFlow<Boolean> = _isCreatePlaylistDialogVisible.asStateFlow()

    private val _isAddToPlaylistDialogVisible = MutableStateFlow(false)
    val isAddToPlaylistDialogVisible: StateFlow<Boolean> = _isAddToPlaylistDialogVisible.asStateFlow()
    val trackForAddToPlaylist = MutableStateFlow<Track?>(null)

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()

    private val _selectedPlaylistTracks = MutableStateFlow<List<Track>>(emptyList())
    val selectedPlaylistTracks: StateFlow<List<Track>> = _selectedPlaylistTracks.asStateFlow()

    private val _visualizerStyle = MutableStateFlow(VisualizerStyle.SPECTRUM_BARS)
    val visualizerStyle: StateFlow<VisualizerStyle> = _visualizerStyle.asStateFlow()

    private val _showLyrics = MutableStateFlow(false)
    val showLyrics: StateFlow<Boolean> = _showLyrics.asStateFlow()

    // Playback Queue
    private val _playQueue = MutableStateFlow<List<Track>>(emptyList())
    val playQueue: StateFlow<List<Track>> = _playQueue.asStateFlow()

    private val _queueIndex = MutableStateFlow(0)
    val queueIndex: StateFlow<Int> = _queueIndex.asStateFlow()

    // Pass-through audio engine states
    val currentTrack: StateFlow<Track?> = audioEngine.currentTrack
    val isPlaying: StateFlow<Boolean> = audioEngine.isPlaying
    val currentPositionMs: StateFlow<Long> = audioEngine.currentPositionMs
    val durationMs: StateFlow<Long> = audioEngine.durationMs
    val playbackSpeed: StateFlow<Float> = audioEngine.playbackSpeed
    val playbackPitch: StateFlow<Float> = audioEngine.playbackPitch
    val repeatMode: StateFlow<RepeatMode> = audioEngine.repeatMode
    val isShuffle: StateFlow<Boolean> = audioEngine.isShuffle
    val spectrumBands: StateFlow<FloatArray> = audioEngine.spectrumBands
    val vuMeterLeft: StateFlow<Float> = audioEngine.vuMeterLeft
    val vuMeterRight: StateFlow<Float> = audioEngine.vuMeterRight
    val activePreset: StateFlow<EqualizerPreset> = audioEngine.activePreset
    val isEqualizerEnabled: StateFlow<Boolean> = audioEngine.isEqualizerEnabled
    val spatialConfig: StateFlow<SpatialAudioConfig> = audioEngine.spatialConfig
    val sleepTimerSecondsLeft: StateFlow<Int?> = audioEngine.sleepTimerSecondsLeft
    val abLoop: StateFlow<ABLoop> = audioEngine.abLoop
    val playbackSettings: StateFlow<PlaybackSettings> = audioEngine.playbackSettings

    // Car / Drive Mode & Advanced Screens
    private val _isCarModeVisible = MutableStateFlow(false)
    val isCarModeVisible: StateFlow<Boolean> = _isCarModeVisible.asStateFlow()

    private val _isSettingsVisible = MutableStateFlow(false)
    val isSettingsVisible: StateFlow<Boolean> = _isSettingsVisible.asStateFlow()

    private val _isStatsDialogVisible = MutableStateFlow(false)
    val isStatsDialogVisible: StateFlow<Boolean> = _isStatsDialogVisible.asStateFlow()

    private val _isBookmarkDialogVisible = MutableStateFlow(false)
    val isBookmarkDialogVisible: StateFlow<Boolean> = _isBookmarkDialogVisible.asStateFlow()

    // Multi-Select Batch Actions State
    private val _isMultiSelectMode = MutableStateFlow(false)
    val isMultiSelectMode: StateFlow<Boolean> = _isMultiSelectMode.asStateFlow()

    private val _selectedTrackIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTrackIds: StateFlow<Set<Long>> = _selectedTrackIds.asStateFlow()

    // Reactive Bookmarks for currently playing track
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTrackBookmarks: StateFlow<List<AudioBookmark>> = currentTrack.flatMapLatest { track ->
        if (track != null) {
            repository.getBookmarksForTrack(track.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Listening Analytics / History Stats
    val listeningStats: StateFlow<ListeningStats?> = allTracks.map { tracks ->
        if (tracks.isEmpty()) null
        else {
            val totalPlays = tracks.sumOf { it.playCount }
            val totalTime = tracks.sumOf { it.playCount.toLong() * it.durationMs }
            val uniquePlayed = tracks.count { it.playCount > 0 }
            val topTracks = tracks.filter { it.playCount > 0 }.sortedByDescending { it.playCount }.take(5)
            val topArtists = tracks.filter { it.playCount > 0 }
                .groupBy { it.artist }
                .mapValues { (_, tList) -> tList.sumOf { it.playCount } }
                .toList()
                .sortedByDescending { it.second }
                .take(5)
            ListeningStats(
                totalPlayCount = totalPlays,
                totalTimeListenedMs = totalTime,
                uniqueTracksPlayed = uniquePlayed,
                topTracks = topTracks,
                topArtists = topArtists
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered & Sorted Tracks
    val filteredTracks: StateFlow<List<Track>> = combine(allTracks, _searchQuery, _sortType, _currentTab) { tracks, query, sort, tab ->
        var list = when (tab) {
            LibraryTab.HI_RES -> tracks.filter { it.isHiRes }
            else -> tracks
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                        it.artist.lowercase().contains(q) ||
                        it.album.lowercase().contains(q) ||
                        it.genre.lowercase().contains(q)
            }
        }
        when (sort) {
            SortType.TITLE -> list.sortedBy { it.title.lowercase() }
            SortType.ARTIST -> list.sortedBy { it.artist.lowercase() }
            SortType.ALBUM -> list.sortedBy { it.album.lowercase() }
            SortType.DURATION -> list.sortedByDescending { it.durationMs }
            SortType.BITRATE -> list.sortedByDescending { it.bitrateKbps }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initDatabaseIfNeeded()
        }

        audioEngine.onTrackCompleted = {
            playNext()
        }
    }

    fun playTrack(track: Track, fromQueue: List<Track>? = null) {
        val queue = fromQueue ?: allTracks.value
        _playQueue.value = queue
        val idx = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        _queueIndex.value = idx
        audioEngine.playTrack(track)
        viewModelScope.launch {
            repository.recordPlay(track.id)
        }
    }

    fun playNext() {
        val queue = _playQueue.value
        if (queue.isEmpty()) return

        val nextIndex = if (audioEngine.isShuffle.value) {
            queue.indices.random()
        } else {
            (_queueIndex.value + 1) % queue.size
        }
        _queueIndex.value = nextIndex
        val track = queue[nextIndex]
        audioEngine.playTrack(track)
        viewModelScope.launch {
            repository.recordPlay(track.id)
        }
    }

    fun playPrevious() {
        val queue = _playQueue.value
        if (queue.isEmpty()) return

        // If played more than 3 seconds, restart current track
        if (audioEngine.currentPositionMs.value > 3000L) {
            audioEngine.seekTo(0L)
            return
        }

        val prevIndex = if (_queueIndex.value > 0) _queueIndex.value - 1 else queue.size - 1
        _queueIndex.value = prevIndex
        val track = queue[prevIndex]
        audioEngine.playTrack(track)
        viewModelScope.launch {
            repository.recordPlay(track.id)
        }
    }

    fun togglePlayPause() = audioEngine.togglePlayPause()
    fun seekTo(ms: Long) = audioEngine.seekTo(ms)
    fun setPlaybackSpeed(speed: Float) = audioEngine.setPlaybackSpeed(speed)
    fun setPlaybackPitch(pitch: Float) = audioEngine.setPlaybackPitch(pitch)
    fun cycleRepeatMode() = audioEngine.cycleRepeatMode()
    fun toggleShuffle() = audioEngine.toggleShuffle()

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            repository.toggleFavorite(track.id, track.isFavorite)
        }
    }

    fun selectTab(tab: LibraryTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortType(sort: SortType) {
        _sortType.value = sort
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    fun setEqualizerVisible(visible: Boolean) {
        _isEqualizerVisible.value = visible
    }

    fun setQueueSheetVisible(visible: Boolean) {
        _isQueueSheetVisible.value = visible
    }

    fun setSleepTimerDialogVisible(visible: Boolean) {
        _isSleepTimerDialogVisible.value = visible
    }

    fun setVisualizerStyle(style: VisualizerStyle) {
        _visualizerStyle.value = style
    }

    fun toggleShowLyrics() {
        _showLyrics.value = !_showLyrics.value
    }

    // --- 360 Spatial Audio ---
    fun setSpatialAudioVisible(visible: Boolean) {
        _isSpatialAudioVisible.value = visible
    }

    fun setSpatialAudioEnabled(enabled: Boolean) = audioEngine.setSpatialAudioEnabled(enabled)
    fun setSpatialAzimuth(azimuth: Float) = audioEngine.setSpatialAzimuth(azimuth)
    fun setSoundstageWidth(width: Float) = audioEngine.setSoundstageWidth(width)
    fun setSpatialDepth(depth: Float) = audioEngine.setSpatialDepth(depth)
    fun setSpatialElevation(elevation: Float) = audioEngine.setSpatialElevation(elevation)
    fun setSpatialRoom(room: SpatialRoom) = audioEngine.setSpatialRoom(room)
    fun toggleAutoOrbit() = audioEngine.toggleAutoOrbit()
    fun setOrbitSpeed(speed: Float) = audioEngine.setOrbitSpeed(speed)

    // --- Equalizer ---
    fun setEqualizerEnabled(enabled: Boolean) = audioEngine.setEqualizerEnabled(enabled)
    fun updateBandGain(bandIdx: Int, gainDb: Float) = audioEngine.updateBandGain(bandIdx, gainDb)
    fun updateBassBoost(percent: Float) = audioEngine.updateBassBoost(percent)
    fun updateTrebleBoost(gainDb: Float) = audioEngine.updateTrebleBoost(gainDb)
    fun updatePreampGain(gainDb: Float) = audioEngine.updatePreampGain(gainDb)
    fun updateVirtualizer(percent: Float) = audioEngine.updateVirtualizer(percent)

    fun selectPreset(preset: EqualizerPreset) {
        audioEngine.applyEqualizerSettings(preset)
    }

    fun saveCustomPreset(name: String) {
        viewModelScope.launch {
            val current = activePreset.value
            val newPreset = current.copy(id = 0, name = name, isBuiltIn = false)
            repository.saveEqualizerPreset(newPreset)
        }
    }

    fun deletePreset(id: Long) {
        viewModelScope.launch {
            repository.deleteEqualizerPreset(id)
        }
    }

    // --- Playlists ---
    fun openPlaylist(playlist: Playlist) {
        _selectedPlaylist.value = playlist
        viewModelScope.launch {
            repository.getTracksForPlaylist(playlist.id).collect { tracks ->
                _selectedPlaylistTracks.value = tracks
            }
        }
    }

    fun closePlaylist() {
        _selectedPlaylist.value = null
        _selectedPlaylistTracks.value = emptyList()
    }

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            repository.createPlaylist(name, description)
            _isCreatePlaylistDialogVisible.value = false
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(id)
            if (_selectedPlaylist.value?.id == id) {
                closePlaylist()
            }
        }
    }

    fun openAddToPlaylist(track: Track) {
        trackForAddToPlaylist.value = track
        _isAddToPlaylistDialogVisible.value = true
    }

    fun closeAddToPlaylist() {
        _isAddToPlaylistDialogVisible.value = false
        trackForAddToPlaylist.value = null
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, trackId)
            closeAddToPlaylist()
        }
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun showCreatePlaylistDialog(show: Boolean) {
        _isCreatePlaylistDialogVisible.value = show
    }

    // --- Tag Editor ---
    fun openTagEditor(track: Track) {
        trackForTagEditing.value = track
        _isTagEditorVisible.value = true
    }

    fun closeTagEditor() {
        _isTagEditorVisible.value = false
        trackForTagEditing.value = null
    }

    fun saveEditedTags(id: Long, title: String, artist: String, album: String, genre: String, year: Int) {
        viewModelScope.launch {
            repository.updateTrackTags(id, title, artist, album, genre, year)
            closeTagEditor()
        }
    }

    // --- Queue Management ---
    fun removeFromQueue(index: Int) {
        val list = _playQueue.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _playQueue.value = list
            if (_queueIndex.value >= list.size) {
                _queueIndex.value = maxOf(0, list.size - 1)
            }
        }
    }

    fun moveQueueItem(from: Int, to: Int) {
        val list = _playQueue.value.toMutableList()
        if (from in list.indices && to in list.indices) {
            val item = list.removeAt(from)
            list.add(to, item)
            _playQueue.value = list
        }
    }

    // --- Sleep Timer ---
    fun startSleepTimer(minutes: Int) {
        audioEngine.startSleepTimer(minutes)
        _isSleepTimerDialogVisible.value = false
    }

    fun cancelSleepTimer() {
        audioEngine.cancelSleepTimer()
        _isSleepTimerDialogVisible.value = false
    }

    fun scanDeviceStorage() {
        viewModelScope.launch {
            repository.scanDeviceMedia()
        }
    }

    // --- Screen / Dialog visibility toggles ---
    fun setCarModeVisible(visible: Boolean) { _isCarModeVisible.value = visible }
    fun setSettingsVisible(visible: Boolean) { _isSettingsVisible.value = visible }
    fun setStatsDialogVisible(visible: Boolean) { _isStatsDialogVisible.value = visible }
    fun setBookmarkDialogVisible(visible: Boolean) { _isBookmarkDialogVisible.value = visible }

    // --- A-B Looping ---
    fun setLoopPointA() = audioEngine.setLoopPointA()
    fun setLoopPointB() = audioEngine.setLoopPointB()
    fun toggleABLoop() = audioEngine.toggleABLoop()
    fun clearABLoop() = audioEngine.clearABLoop()

    // --- Advanced Audio Settings ---
    fun setBalance(balance: Float) = audioEngine.setBalance(balance)
    fun setMono(isMono: Boolean) = audioEngine.setMono(isMono)
    fun setCrossfadeSeconds(sec: Int) = audioEngine.setCrossfadeSeconds(sec)
    fun setGapless(gapless: Boolean) = audioEngine.setGapless(gapless)
    fun setReplayGain(enabled: Boolean) = audioEngine.setReplayGain(enabled)
    fun setThemeAccent(accent: ThemeAccent) = audioEngine.setThemeAccent(accent)

    // --- Audio Bookmarks ---
    fun addBookmark(label: String) {
        val track = currentTrack.value ?: return
        val pos = currentPositionMs.value
        viewModelScope.launch {
            repository.addBookmark(track.id, pos, label.ifBlank { "Mark @ ${pos / 1000}s" })
        }
    }

    fun deleteBookmark(id: Long) {
        viewModelScope.launch {
            repository.deleteBookmark(id)
        }
    }

    fun seekToBookmark(posMs: Long) {
        seekTo(posMs)
    }

    // --- Multi-Select Batch Operations ---
    fun toggleMultiSelectMode(enable: Boolean? = null) {
        val target = enable ?: !_isMultiSelectMode.value
        _isMultiSelectMode.value = target
        if (!target) {
            _selectedTrackIds.value = emptySet()
        }
    }

    fun toggleTrackSelection(trackId: Long) {
        val current = _selectedTrackIds.value.toMutableSet()
        if (current.contains(trackId)) {
            current.remove(trackId)
        } else {
            current.add(trackId)
        }
        _selectedTrackIds.value = current
        if (!_isMultiSelectMode.value && current.isNotEmpty()) {
            _isMultiSelectMode.value = true
        }
    }

    fun selectAllFilteredTracks(tracks: List<Track>) {
        _selectedTrackIds.value = tracks.map { it.id }.toSet()
    }

    fun clearTrackSelection() {
        _selectedTrackIds.value = emptySet()
    }

    fun batchPlaySelected(tracks: List<Track>) {
        val selected = tracks.filter { it.id in _selectedTrackIds.value }
        if (selected.isNotEmpty()) {
            playTrack(selected.first(), selected)
            clearTrackSelection()
            _isMultiSelectMode.value = false
        }
    }

    fun batchAddToQueue(tracks: List<Track>) {
        val selected = tracks.filter { it.id in _selectedTrackIds.value }
        if (selected.isNotEmpty()) {
            _playQueue.value = _playQueue.value + selected
            clearTrackSelection()
            _isMultiSelectMode.value = false
        }
    }

    fun batchAddToPlaylist(playlistId: Long) {
        val ids = _selectedTrackIds.value.toList()
        if (ids.isNotEmpty()) {
            viewModelScope.launch {
                repository.addTracksToPlaylist(playlistId, ids)
                clearTrackSelection()
                _isMultiSelectMode.value = false
            }
        }
    }

    fun batchToggleFavorite(tracks: List<Track>) {
        val selected = tracks.filter { it.id in _selectedTrackIds.value }
        viewModelScope.launch {
            selected.forEach { track ->
                repository.toggleFavorite(track.id, track.isFavorite)
            }
            clearTrackSelection()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
