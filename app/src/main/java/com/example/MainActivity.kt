package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import com.example.data.model.Playlist
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.BookmarkDialog
import com.example.ui.components.CarModeScreen
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.EqualizerScreen
import com.example.ui.components.LibraryScreen
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.NowPlayingSheet
import com.example.ui.components.PlaylistDetailScreen
import com.example.ui.components.QueueSheet
import com.example.ui.components.SettingsScreen
import com.example.ui.components.SleepTimerDialog
import com.example.ui.components.SpatialAudioScreen
import com.example.ui.components.StatsDialog
import com.example.ui.components.TagEditorDialog
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MusicPlayerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MusicPlayerViewModel = viewModel()
            val playbackSettings by viewModel.playbackSettings.collectAsStateWithLifecycle()
            MyApplicationTheme(accentColor = Color(playbackSettings.accent.hexColor)) {
                PulseAmpApp(viewModel)
            }
        }
    }
}

@Composable
fun PulseAmpApp(viewModel: MusicPlayerViewModel = viewModel()) {
    // Collect UI state
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortType by viewModel.sortType.collectAsStateWithLifecycle()
    val allTracks by viewModel.allTracks.collectAsStateWithLifecycle()
    val filteredTracks by viewModel.filteredTracks.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsStateWithLifecycle()
    val selectedPlaylistTracks by viewModel.selectedPlaylistTracks.collectAsStateWithLifecycle()

    // Audio Engine State
    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPositionMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
    val playbackPitch by viewModel.playbackPitch.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val isShuffle by viewModel.isShuffle.collectAsStateWithLifecycle()
    val spectrumBands by viewModel.spectrumBands.collectAsStateWithLifecycle()
    val vuLeft by viewModel.vuMeterLeft.collectAsStateWithLifecycle()
    val vuRight by viewModel.vuMeterRight.collectAsStateWithLifecycle()

    // Overlay visibility states
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsStateWithLifecycle()
    val isEqualizerVisible by viewModel.isEqualizerVisible.collectAsStateWithLifecycle()
    val isSpatialAudioVisible by viewModel.isSpatialAudioVisible.collectAsStateWithLifecycle()
    val isQueueSheetVisible by viewModel.isQueueSheetVisible.collectAsStateWithLifecycle()
    val isSleepTimerDialogVisible by viewModel.isSleepTimerDialogVisible.collectAsStateWithLifecycle()
    val isTagEditorVisible by viewModel.isTagEditorVisible.collectAsStateWithLifecycle()
    val trackForTagEditing by viewModel.trackForTagEditing.collectAsStateWithLifecycle()
    val isCreatePlaylistDialogVisible by viewModel.isCreatePlaylistDialogVisible.collectAsStateWithLifecycle()
    val isAddToPlaylistDialogVisible by viewModel.isAddToPlaylistDialogVisible.collectAsStateWithLifecycle()
    val trackForAddToPlaylist by viewModel.trackForAddToPlaylist.collectAsStateWithLifecycle()
    val visualizerStyle by viewModel.visualizerStyle.collectAsStateWithLifecycle()
    val showLyrics by viewModel.showLyrics.collectAsStateWithLifecycle()

    // Equalizer, Spatial & Queue State
    val allPresets by viewModel.allPresets.collectAsStateWithLifecycle()
    val activePreset by viewModel.activePreset.collectAsStateWithLifecycle()
    val isEqualizerEnabled by viewModel.isEqualizerEnabled.collectAsStateWithLifecycle()
    val spatialConfig by viewModel.spatialConfig.collectAsStateWithLifecycle()
    val playQueue by viewModel.playQueue.collectAsStateWithLifecycle()
    val queueIndex by viewModel.queueIndex.collectAsStateWithLifecycle()
    val sleepTimerSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsStateWithLifecycle()

    // Advanced & Feature States
    val abLoop by viewModel.abLoop.collectAsStateWithLifecycle()
    val playbackSettings by viewModel.playbackSettings.collectAsStateWithLifecycle()
    val isCarModeVisible by viewModel.isCarModeVisible.collectAsStateWithLifecycle()
    val isSettingsVisible by viewModel.isSettingsVisible.collectAsStateWithLifecycle()
    val isStatsDialogVisible by viewModel.isStatsDialogVisible.collectAsStateWithLifecycle()
    val isBookmarkDialogVisible by viewModel.isBookmarkDialogVisible.collectAsStateWithLifecycle()
    val currentTrackBookmarks by viewModel.currentTrackBookmarks.collectAsStateWithLifecycle()
    val listeningStats by viewModel.listeningStats.collectAsStateWithLifecycle()
    val isMultiSelectMode by viewModel.isMultiSelectMode.collectAsStateWithLifecycle()
    val selectedTrackIds by viewModel.selectedTrackIds.collectAsStateWithLifecycle()

    // Gracefully request media storage permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scanDeviceStorage()
        }
    }

    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)
    }

    // Predictive/Back button handling
    BackHandler(enabled = isCarModeVisible || isSettingsVisible || isEqualizerVisible || isSpatialAudioVisible || isQueueSheetVisible || isNowPlayingExpanded || selectedPlaylist != null || isMultiSelectMode) {
        when {
            isCarModeVisible -> viewModel.setCarModeVisible(false)
            isSettingsVisible -> viewModel.setSettingsVisible(false)
            isEqualizerVisible -> viewModel.setEqualizerVisible(false)
            isSpatialAudioVisible -> viewModel.setSpatialAudioVisible(false)
            isQueueSheetVisible -> viewModel.setQueueSheetVisible(false)
            isNowPlayingExpanded -> viewModel.setNowPlayingExpanded(false)
            selectedPlaylist != null -> viewModel.closePlaylist()
            isMultiSelectMode -> viewModel.toggleMultiSelectMode(false)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = CarbonBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CarbonBlack)
        ) {
            // Main Content: Library or Playlist Details
            val activePlaylist = selectedPlaylist
            if (activePlaylist != null) {
                // If special smart playlist id (-1 = Favorites, -2 = Hi-Res)
                val tracksForView = when (activePlaylist.id) {
                    -1L -> allTracks.filter { it.isFavorite }
                    -2L -> allTracks.filter { it.isHiRes }
                    else -> selectedPlaylistTracks
                }
                PlaylistDetailScreen(
                    playlist = activePlaylist,
                    tracks = tracksForView,
                    currentPlayingTrack = currentTrack,
                    isPlaying = isPlaying,
                    onBack = { viewModel.closePlaylist() },
                    onPlayTrack = { track, list -> viewModel.playTrack(track, list) },
                    onPlayAll = {
                        if (tracksForView.isNotEmpty()) {
                            viewModel.playTrack(tracksForView.first(), tracksForView)
                        }
                    },
                    onShuffleAll = {
                        if (tracksForView.isNotEmpty()) {
                            viewModel.toggleShuffle()
                            viewModel.playTrack(tracksForView.random(), tracksForView)
                        }
                    },
                    onRemoveTrack = { trackId ->
                        if (activePlaylist.id > 0) {
                            viewModel.removeTrackFromPlaylist(activePlaylist.id, trackId)
                        } else if (activePlaylist.id == -1L) {
                            val t = allTracks.find { it.id == trackId }
                            if (t != null) viewModel.toggleFavorite(t)
                        }
                    },
                    onDeletePlaylist = { id -> viewModel.deletePlaylist(id) },
                    onOpenTagEditor = { viewModel.openTagEditor(it) },
                    onOpenAddToPlaylist = { viewModel.openAddToPlaylist(it) }
                )
            } else {
                LibraryScreen(
                    currentTab = currentTab,
                    tracks = filteredTracks,
                    allTracks = allTracks,
                    playlists = allPlaylists,
                    searchQuery = searchQuery,
                    sortType = sortType,
                    currentPlayingTrack = currentTrack,
                    isPlaying = isPlaying,
                    onSelectTab = { viewModel.selectTab(it) },
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onSortChange = { viewModel.setSortType(it) },
                    onPlayTrack = { track, list -> viewModel.playTrack(track, list) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onOpenPlaylist = { viewModel.openPlaylist(it) },
                    onCreatePlaylist = { viewModel.showCreatePlaylistDialog(true) },
                    onOpenEqualizer = { viewModel.setEqualizerVisible(true) },
                    onOpenSpatialAudio = { viewModel.setSpatialAudioVisible(true) },
                    onOpenTagEditor = { viewModel.openTagEditor(it) },
                    onOpenAddToPlaylist = { viewModel.openAddToPlaylist(it) },
                    onScanStorage = { viewModel.scanDeviceStorage() },
                    onOpenSettings = { viewModel.setSettingsVisible(true) },
                    onOpenCarMode = { viewModel.setCarModeVisible(true) },
                    isMultiSelectMode = isMultiSelectMode,
                    selectedTrackIds = selectedTrackIds,
                    onToggleMultiSelectMode = { viewModel.toggleMultiSelectMode(it) },
                    onToggleTrackSelection = { viewModel.toggleTrackSelection(it) },
                    onSelectAllTracks = { viewModel.selectAllFilteredTracks(it) },
                    onClearSelection = { viewModel.clearTrackSelection() },
                    onBatchPlay = { viewModel.batchPlaySelected(it) },
                    onBatchAddToQueue = { viewModel.batchAddToQueue(it) },
                    onBatchAddToPlaylist = {
                        val firstId = selectedTrackIds.firstOrNull()
                        val firstTrack = allTracks.find { it.id == firstId }
                        if (firstTrack != null) {
                            viewModel.openAddToPlaylist(firstTrack)
                        }
                    },
                    onBatchFavorite = { viewModel.batchToggleFavorite(it) }
                )
            }

            // Persistent Mini Player docked at bottom
            if (currentTrack != null && !isNowPlayingExpanded && !isEqualizerVisible && !isSpatialAudioVisible && !isQueueSheetVisible) {
                MiniPlayerBar(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    positionMs = currentPositionMs,
                    durationMs = durationMs,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onSkipNext = { viewModel.playNext() },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onExpand = { viewModel.setNowPlayingExpanded(true) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // Full-screen Now Playing Overlay
            AnimatedVisibility(
                visible = isNowPlayingExpanded,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NowPlayingSheet(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    positionMs = currentPositionMs,
                    durationMs = durationMs,
                    playbackSpeed = playbackSpeed,
                    playbackPitch = playbackPitch,
                    repeatMode = repeatMode,
                    isShuffle = isShuffle,
                    spectrumBands = spectrumBands,
                    vuLeft = vuLeft,
                    vuRight = vuRight,
                    visualizerStyle = visualizerStyle,
                    showLyrics = showLyrics,
                    sleepTimerSecondsLeft = sleepTimerSecondsLeft,
                    isSpatialActive = spatialConfig.isEnabled,
                    queueIndex = queueIndex,
                    queueSize = playQueue.size,
                    onCollapse = { viewModel.setNowPlayingExpanded(false) },
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onSeekTo = { viewModel.seekTo(it) },
                    onPlayNext = { viewModel.playNext() },
                    onPlayPrevious = { viewModel.playPrevious() },
                    onToggleRepeat = { viewModel.cycleRepeatMode() },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onOpenEqualizer = { viewModel.setEqualizerVisible(true) },
                    onOpenSpatialAudio = { viewModel.setSpatialAudioVisible(true) },
                    onOpenQueue = { viewModel.setQueueSheetVisible(true) },
                    onOpenSleepTimer = { viewModel.setSleepTimerDialogVisible(true) },
                    onOpenTagEditor = { viewModel.openTagEditor(it) },
                    onOpenAddToPlaylist = { viewModel.openAddToPlaylist(it) },
                    onCycleVisualizerStyle = {
                        val styles = com.example.data.model.VisualizerStyle.entries
                        val next = styles[(visualizerStyle.ordinal + 1) % styles.size]
                        viewModel.setVisualizerStyle(next)
                    },
                    onToggleLyrics = { viewModel.toggleShowLyrics() },
                    onSetPlaybackSpeed = { viewModel.setPlaybackSpeed(it) },
                    abLoop = abLoop,
                    onSetLoopA = { viewModel.setLoopPointA() },
                    onSetLoopB = { viewModel.setLoopPointB() },
                    onToggleLoop = { viewModel.toggleABLoop() },
                    onClearLoop = { viewModel.clearABLoop() },
                    onOpenBookmarks = { viewModel.setBookmarkDialogVisible(true) },
                    onOpenCarMode = { viewModel.setCarModeVisible(true) }
                )
            }

            // Poweramp 10-Band Equalizer Overlay
            AnimatedVisibility(
                visible = isEqualizerVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                EqualizerScreen(
                    activePreset = activePreset,
                    isEqualizerEnabled = isEqualizerEnabled,
                    presets = allPresets,
                    onToggleEnabled = { viewModel.setEqualizerEnabled(it) },
                    onBandChange = { band, gain -> viewModel.updateBandGain(band, gain) },
                    onBassChange = { viewModel.updateBassBoost(it) },
                    onTrebleChange = { viewModel.updateTrebleBoost(it) },
                    onPreampChange = { viewModel.updatePreampGain(it) },
                    onVirtualizerChange = { viewModel.updateVirtualizer(it) },
                    onSelectPreset = { viewModel.selectPreset(it) },
                    onSaveCustomPreset = { viewModel.saveCustomPreset(it) },
                    onDeletePreset = { viewModel.deletePreset(it) },
                    onClose = { viewModel.setEqualizerVisible(false) }
                )
            }

            // 360 Spatial Audio Overlay
            AnimatedVisibility(
                visible = isSpatialAudioVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                SpatialAudioScreen(
                    config = spatialConfig,
                    onToggleEnabled = { viewModel.setSpatialAudioEnabled(it) },
                    onAzimuthChange = { viewModel.setSpatialAzimuth(it) },
                    onSoundstageWidthChange = { viewModel.setSoundstageWidth(it) },
                    onDepthChange = { viewModel.setSpatialDepth(it) },
                    onElevationChange = { viewModel.setSpatialElevation(it) },
                    onRoomChange = { viewModel.setSpatialRoom(it) },
                    onToggleAutoOrbit = { viewModel.toggleAutoOrbit() },
                    onOrbitSpeedChange = { viewModel.setOrbitSpeed(it) },
                    onClose = { viewModel.setSpatialAudioVisible(false) }
                )
            }

            // Play Queue Overlay
            AnimatedVisibility(
                visible = isQueueSheetVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                QueueSheet(
                    queue = playQueue,
                    currentIndex = queueIndex,
                    isPlaying = isPlaying,
                    onSelectTrack = { viewModel.playTrack(it, playQueue) },
                    onRemoveFromQueue = { viewModel.removeFromQueue(it) },
                    onMoveQueueItem = { from, to -> viewModel.moveQueueItem(from, to) },
                    onSaveAsPlaylist = { viewModel.showCreatePlaylistDialog(true) },
                    onClose = { viewModel.setQueueSheetVisible(false) }
                )
            }

            // Audio Engine Settings Overlay
            AnimatedVisibility(
                visible = isSettingsVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                SettingsScreen(
                    settings = playbackSettings,
                    onBalanceChange = { viewModel.setBalance(it) },
                    onMonoToggle = { viewModel.setMono(it) },
                    onCrossfadeChange = { viewModel.setCrossfadeSeconds(it) },
                    onGaplessToggle = { viewModel.setGapless(it) },
                    onReplayGainToggle = { viewModel.setReplayGain(it) },
                    onThemeAccentChange = { viewModel.setThemeAccent(it) },
                    onOpenStats = { viewModel.setStatsDialogVisible(true) },
                    onScanStorage = { viewModel.scanDeviceStorage() },
                    onClose = { viewModel.setSettingsVisible(false) }
                )
            }

            // Car / Drive Mode Fullscreen Overlay
            AnimatedVisibility(
                visible = isCarModeVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                CarModeScreen(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    positionMs = currentPositionMs,
                    durationMs = durationMs,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    onPlayNext = { viewModel.playNext() },
                    onPlayPrevious = { viewModel.playPrevious() },
                    onSeekTo = { viewModel.seekTo(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onClose = { viewModel.setCarModeVisible(false) }
                )
            }
        }
    }

    // Modal Dialogs
    if (isTagEditorVisible) {
        val t = trackForTagEditing
        if (t != null) {
            TagEditorDialog(
                track = t,
                onDismiss = { viewModel.closeTagEditor() },
                onSave = { id, title, artist, album, genre, year ->
                    viewModel.saveEditedTags(id, title, artist, album, genre, year)
                }
            )
        }
    }

    if (isSleepTimerDialogVisible) {
        SleepTimerDialog(
            secondsLeft = sleepTimerSecondsLeft,
            onDismiss = { viewModel.setSleepTimerDialogVisible(false) },
            onStartTimer = { viewModel.startSleepTimer(it) },
            onCancelTimer = { viewModel.cancelSleepTimer() }
        )
    }

    if (isCreatePlaylistDialogVisible) {
        CreatePlaylistDialog(
            onDismiss = { viewModel.showCreatePlaylistDialog(false) },
            onCreate = { name, desc -> viewModel.createPlaylist(name, desc) }
        )
    }

    if (isAddToPlaylistDialogVisible) {
        val t = trackForAddToPlaylist
        if (t != null) {
            AddToPlaylistDialog(
                track = t,
                playlists = allPlaylists,
                onDismiss = { viewModel.closeAddToPlaylist() },
                onSelectPlaylist = { pId -> viewModel.addTrackToPlaylist(pId, t.id) },
                onCreateNewPlaylist = {
                    viewModel.closeAddToPlaylist()
                    viewModel.showCreatePlaylistDialog(true)
                }
            )
        }
    }

    if (isBookmarkDialogVisible) {
        BookmarkDialog(
            track = currentTrack,
            currentPositionMs = currentPositionMs,
            bookmarks = currentTrackBookmarks,
            onDismiss = { viewModel.setBookmarkDialogVisible(false) },
            onAddBookmark = { viewModel.addBookmark(it) },
            onDeleteBookmark = { viewModel.deleteBookmark(it) },
            onSeekToBookmark = { viewModel.seekToBookmark(it) }
        )
    }

    if (isStatsDialogVisible) {
        StatsDialog(
            stats = listeningStats,
            onDismiss = { viewModel.setStatsDialogVisible(false) }
        )
    }
}
