package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ABLoop
import com.example.data.model.RepeatMode
import com.example.data.model.Track
import com.example.data.model.VisualizerStyle
import com.example.ui.theme.AmberVUGlow
import com.example.ui.theme.CarbonBlack
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HiResGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NowPlayingSheet(
    track: Track?,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    playbackSpeed: Float,
    playbackPitch: Float,
    repeatMode: RepeatMode,
    isShuffle: Boolean,
    spectrumBands: FloatArray,
    vuLeft: Float,
    vuRight: Float,
    visualizerStyle: VisualizerStyle,
    showLyrics: Boolean,
    sleepTimerSecondsLeft: Int?,
    isSpatialActive: Boolean,
    queueIndex: Int,
    queueSize: Int,
    onCollapse: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenSpatialAudio: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onOpenTagEditor: (Track) -> Unit,
    onOpenAddToPlaylist: (Track) -> Unit,
    onCycleVisualizerStyle: () -> Unit,
    onToggleLyrics: () -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    abLoop: ABLoop = ABLoop(),
    onSetLoopA: () -> Unit = {},
    onSetLoopB: () -> Unit = {},
    onToggleLoop: () -> Unit = {},
    onClearLoop: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
    onOpenCarMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (track == null) return

    var showDetailsDialog by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var isScrubbing by remember { mutableStateOf(false) }
    var scrubPositionMs by remember { mutableFloatStateOf(positionMs.toFloat()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCollapse, modifier = Modifier.testTag("now_playing_collapse_button")) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Collapse", tint = TextPrimary, modifier = Modifier.size(32.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PLAYING ${queueIndex + 1} OF $queueSize".uppercase(),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Poweramp Hi-Res Engine",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Bookmarks button
                IconButton(onClick = onOpenBookmarks, modifier = Modifier.testTag("header_bookmarks_button")) {
                    Icon(
                        Icons.Default.Bookmark,
                        contentDescription = "Bookmarks",
                        tint = NeonCyan
                    )
                }

                // Car Mode button
                IconButton(onClick = onOpenCarMode, modifier = Modifier.testTag("header_car_mode_button")) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = "Drive Mode",
                        tint = TextSecondary
                    )
                }

                // 360 Spatial Audio button
                IconButton(onClick = onOpenSpatialAudio, modifier = Modifier.testTag("header_spatial_audio_button")) {
                    Icon(
                        Icons.Default.SurroundSound,
                        contentDescription = "360 Spatial Audio",
                        tint = if (isSpatialActive) NeonCyan else TextMuted
                    )
                }

                // Sleep timer badge/button
                IconButton(onClick = onOpenSleepTimer, modifier = Modifier.testTag("header_sleep_timer_button")) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Sleep Timer",
                        tint = if (sleepTimerSecondsLeft != null) NeonCyan else TextMuted
                    )
                }

                // Audio specs info button
                IconButton(onClick = { showDetailsDialog = true }, modifier = Modifier.testTag("header_audio_info_button")) {
                    Icon(Icons.Default.Info, contentDescription = "Audio Engine Pipeline", tint = HiResGold)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Audiophile Format Badge Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceElevated)
                .border(1.dp, if (isSpatialActive) NeonCyan.copy(alpha = 0.8f) else HiResGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .clickable { showDetailsDialog = true }
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isSpatialActive) NeonCyan else if (track.isHiRes) HiResGold else NeonCyan)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isSpatialActive) {
                        "360° AUDIO • ${track.format} ${track.bitDepth}-BIT • BINAURAL"
                    } else {
                        "${track.format} • ${track.bitDepth}-BIT / ${track.sampleRate / 1000}kHz • ${track.bitrateKbps}kbps"
                    },
                    color = if (isSpatialActive) NeonCyan else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Center Art / Visualizer / Lyrics Container
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .aspectRatio(1f)
                .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = NeonCyan)
                .clip(RoundedCornerShape(20.dp))
                .background(DeepCharcoal),
            contentAlignment = Alignment.Center
        ) {
            // Album Art
            if (track.albumArtResId != null) {
                Image(
                    painter = painterResource(id = track.albumArtResId),
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (!track.albumArtUri.isNullOrEmpty()) {
                AsyncImage(
                    model = track.albumArtUri,
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = NeonCyan.copy(alpha = 0.4f),
                    modifier = Modifier.size(96.dp)
                )
            }

            // Waveform Visualizer Overlay
            if (!showLyrics) {
                WaveformVisualizer(
                    bands = spectrumBands,
                    vuLeft = vuLeft,
                    vuRight = vuRight,
                    isPlaying = isPlaying,
                    style = visualizerStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .align(Alignment.BottomCenter)
                )
            } else {
                // Lyrics Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CarbonBlack.copy(alpha = 0.88f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "LYRICS",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        val lines = track.lyrics?.lines() ?: listOf("No synced lyrics available for this track.")
                        lines.forEach { line ->
                            Text(
                                text = line.replace(Regex("\\[.*?\\]"), "").trim(),
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Quick mode toggles on art top right
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(onClick = onCycleVisualizerStyle, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Equalizer, contentDescription = "Change Visualizer Style", tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onToggleLyrics, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Subtitles,
                        contentDescription = "Toggle Lyrics",
                        tint = if (showLyrics) NeonPink else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Track Title & Artist Metadata + Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${track.artist} — ${track.album}",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row {
                IconButton(onClick = { onToggleFavorite(track) }, modifier = Modifier.testTag("np_fav_button")) {
                    Icon(
                        imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (track.isFavorite) NeonPink else TextSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                IconButton(onClick = { onOpenAddToPlaylist(track) }, modifier = Modifier.testTag("np_add_playlist_button")) {
                    Icon(Icons.Default.PlaylistAdd, contentDescription = "Add to Playlist", tint = TextSecondary, modifier = Modifier.size(26.dp))
                }

                IconButton(onClick = { onOpenTagEditor(track) }, modifier = Modifier.testTag("np_edit_tag_button")) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Tags", tint = TextSecondary, modifier = Modifier.size(22.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Progress Bar & Waveform Scrubbing Track
        val currentMs = if (isScrubbing) scrubPositionMs.toLong() else positionMs
        val totalMs = durationMs.coerceAtLeast(1L)
        val progress = (currentMs.toFloat() / totalMs).coerceIn(0f, 1f)

        Slider(
            value = progress,
            onValueChange = {
                isScrubbing = true
                scrubPositionMs = it * totalMs
            },
            onValueChangeFinished = {
                isScrubbing = false
                onSeekTo(scrubPositionMs.toLong())
            },
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = SurfaceBorder
            ),
            modifier = Modifier.fillMaxWidth().testTag("now_playing_seek_slider")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val curSec = currentMs / 1000
            val remSec = (totalMs - currentMs) / 1000
            Text(
                text = "%d:%02d".format(curSec / 60, curSec % 60),
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "-%d:%02d".format(remSec / 60, remSec % 60),
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(Modifier.height(12.dp))

        // Main Poweramp Playback Controls (Shuffle, Prev, Play/Pause, Next, Repeat)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleShuffle, modifier = Modifier.testTag("np_shuffle_button")) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffle) NeonCyan else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }

            IconButton(onClick = onPlayPrevious, modifier = Modifier.size(54.dp).testTag("np_previous_button")) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = TextPrimary, modifier = Modifier.size(36.dp))
            }

            // Poweramp Neon Play/Pause Button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NeonCyan, ElectricViolet),
                            radius = 120f
                        )
                    )
                    .clickable { onTogglePlayPause() }
                    .testTag("np_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = CarbonBlack,
                    modifier = Modifier.size(44.dp)
                )
            }

            IconButton(onClick = onPlayNext, modifier = Modifier.size(54.dp).testTag("np_next_button")) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = TextPrimary, modifier = Modifier.size(36.dp))
            }

            IconButton(onClick = onToggleRepeat, modifier = Modifier.testTag("np_repeat_button")) {
                Icon(
                    imageVector = when (repeatMode) {
                        RepeatMode.OFF -> Icons.Default.Repeat
                        RepeatMode.ALL -> Icons.Default.Repeat
                        RepeatMode.ONE -> Icons.Default.RepeatOne
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode != RepeatMode.OFF) NeonCyan else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // A-B Looping Control Strip
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (abLoop.isActive) NeonCyan.copy(alpha = 0.12f) else DeepCharcoal
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (abLoop.isActive) NeonCyan else SurfaceBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("np_ab_loop_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Point A
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "A: " + (abLoop.pointA?.let { "%d:%02d".format((it / 1000) / 60, (it / 1000) % 60) } ?: "--:--"),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (abLoop.pointA != null) NeonCyan else TextMuted
                    )
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceElevated,
                        modifier = Modifier
                            .clickable { onSetLoopA() }
                            .testTag("np_loop_set_a_button")
                    ) {
                        Text(
                            text = "SET A",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Loop Active Toggle Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (abLoop.isActive) NeonCyan else SurfaceDark,
                    modifier = Modifier
                        .clickable { onToggleLoop() }
                        .testTag("np_toggle_ab_loop_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AllInclusive,
                            contentDescription = "A-B Loop",
                            tint = if (abLoop.isActive) CarbonBlack else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (abLoop.isActive) "LOOP ON" else "A-B LOOP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (abLoop.isActive) CarbonBlack else TextSecondary
                        )
                    }
                }

                // Point B
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceElevated,
                        modifier = Modifier
                            .clickable { onSetLoopB() }
                            .testTag("np_loop_set_b_button")
                    ) {
                        Text(
                            text = "SET B",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "B: " + (abLoop.pointB?.let { "%d:%02d".format((it / 1000) / 60, (it / 1000) % 60) } ?: "--:--"),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (abLoop.pointB != null) NeonCyan else TextMuted
                    )
                    if (abLoop.pointA != null || abLoop.pointB != null) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Loop",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onClearLoop() }
                                .testTag("np_clear_ab_loop_button")
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Bottom Audiophile Quick Actions: Equalizer, Queue, Speed Dial
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DeepCharcoal)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Equalizer Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenEqualizer() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(6.dp))
                Text("EQ", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // 360 Spatial Audio Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenSpatialAudio() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.SurroundSound,
                    contentDescription = null,
                    tint = if (isSpatialActive) NeonCyan else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "360°",
                    color = if (isSpatialActive) NeonCyan else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Speed Dial Dropdown
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showSpeedMenu = true }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = AmberVUGlow, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("%.2fx".format(playbackSpeed), color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                DropdownMenu(
                    expanded = showSpeedMenu,
                    onDismissRequest = { showSpeedMenu = false },
                    modifier = Modifier.background(DeepCharcoal)
                ) {
                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        DropdownMenuItem(
                            text = { Text("${speed}x", color = if (playbackSpeed == speed) NeonCyan else TextPrimary) },
                            onClick = {
                                onSetPlaybackSpeed(speed)
                                showSpeedMenu = false
                            }
                        )
                    }
                }
            }

            // Queue Sheet Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenQueue() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.QueueMusic, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(6.dp))
                Text("QUEUE ($queueSize)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDetailsDialog) {
        TrackDetailsDialog(track = track, onDismiss = { showDetailsDialog = false })
    }
}
